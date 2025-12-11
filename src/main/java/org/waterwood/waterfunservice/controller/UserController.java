package org.waterwood.waterfunservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.dto.request.user.UpdateUserProfileRequest;
import org.waterwood.api.ApiResponse;
import org.waterwood.waterfunservice.dto.response.user.UserInfoResponse;
import org.waterwood.waterfunservice.dto.response.user.UserProfileResponse;
import org.waterwood.waterfunservicecore.api.PostPolicyDto;
import org.waterwood.waterfunservicecore.api.resp.CloudResourcePresignedUrlResp;
import org.waterwood.waterfunservicecore.entity.Permission;
import org.waterwood.waterfunservice.infrastructure.mapper.UserMapper;
import org.waterwood.waterfunservice.infrastructure.mapper.UserProfileMapper;
import org.waterwood.waterfunservicecore.entity.user.User;
import org.waterwood.waterfunservicecore.entity.user.UserProfile;
import org.waterwood.waterfunservicecore.infrastructure.security.AuthContextHelper;
import org.waterwood.waterfunservice.service.user.impl.UserProfileServiceImpl;
import org.waterwood.waterfunservice.service.user.UserService;
import org.waterwood.waterfunservicecore.services.storage.CloudFileService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;
    private final UserProfileServiceImpl userProfileService;
    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final CloudFileService cloudFileService;

    @GetMapping("userInfo")
    public ApiResponse<UserInfoResponse> getUserInfo(){
        User user = userService.getUserById(AuthContextHelper.getCurrentUserId());
        UserInfoResponse res = userMapper.toUserInfoResponse(user);
        res.setPasswordHash(user.getPasswordHash() != null);
        return ApiResponse.success(res);
    }
    @PutMapping("/updateProfile")
    public ApiResponse<Void> updateProfile(@RequestBody @Valid UpdateUserProfileRequest body){
        userProfileService.updateProfileByDto(body);
        return ApiResponse.success();
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(){
        UserProfile up = userProfileService.getUserProfile();
        UserProfileResponse res = userProfileMapper.toResponse(up);
        res.setAvatar(
                userProfileService.getUserAvatar()
        );
        return ApiResponse.success(res);
    }


    @GetMapping("/avatar/upload")
    public ApiResponse<PostPolicyDto> updateAvatar(@RequestParam String suffix){
        return ApiResponse.success(userProfileService.getUploadPolicyAndSaveAvatar(suffix));
    }

    @GetMapping("/avatar")
    public ApiResponse<CloudResourcePresignedUrlResp> getAvatar(){
        return ApiResponse.success(
                userProfileService.getUserAvatar()
        );
    }


    @GetMapping("/permissions")
    public ApiResponse<Set<String>> getPermissions(){
        long userId = AuthContextHelper.getCurrentUserId();
        Set<String> permCodes = userService.getUserPermissions(userId)
                .stream().map(Permission::getCode)
                .collect(Collectors.toSet());
        return ApiResponse.success(permCodes);
    }
}
