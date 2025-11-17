package org.waterwood.waterfunservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.dto.request.user.UpdateUserProfileRequest;
import org.waterwood.waterfunservice.dto.request.user.UserPwdUpdateRequestBody;
import org.waterwood.waterfunservice.dto.response.comm.ApiResponse;
import org.waterwood.waterfunservice.dto.response.user.UserInfoResponse;
import org.waterwood.waterfunservice.dto.response.user.UserProfileResponse;
import org.waterwood.waterfunservice.entity.Permission;
import org.waterwood.waterfunservice.infrastructure.mapper.UserMapper;
import org.waterwood.waterfunservice.infrastructure.mapper.UserProfileMapper;
import org.waterwood.waterfunservice.infrastructure.utils.context.ThreadLocalUtil;
import org.waterwood.waterfunservice.service.user.impl.UserProfileServiceImpl;
import org.waterwood.waterfunservice.service.user.UserService;

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

    @GetMapping("userInfo")
    public ApiResponse<UserInfoResponse> getUserInfo(@RequestParam long userId){
        return ApiResponse.success(
                userMapper.toUserInfoResponse(
                        userService.getUserById(userId)
                )
        );
    }
    @PutMapping("/updateProfile")
    public ApiResponse<Void> updateProfile(@RequestBody @Valid UpdateUserProfileRequest body){
        userProfileService.updateProfile(userProfileMapper.toEntity(body));
        return ApiResponse.success();
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(@RequestParam long userId){
        return ApiResponse.success(userProfileMapper.toResponse(
                userProfileService.getUserProfile(userId)
        ));
    }

    @PatchMapping("/updateAvatar")
    public ApiResponse<Void> updateAvatar(@RequestParam @URL String avatarUrl){
        userProfileService.updateAvatar(avatarUrl);
        return ApiResponse.success();
    }

    @PatchMapping("/updatePwd")
    public ApiResponse<Void> updatePwd(@RequestBody @Valid UserPwdUpdateRequestBody userPwdUpdateRequestBody){
        userService.updatePwd(userPwdUpdateRequestBody);
        return ApiResponse.success();
    }

    @GetMapping("/permissions")
    public ApiResponse<Set<String>> getPermissions(){
        long userId = ThreadLocalUtil.getCurrentUserId();
        Set<String> permCodes = userService.getUserPermissions(userId)
                .stream().map(Permission::getCode)
                .collect(Collectors.toSet());
        return ApiResponse.success(permCodes);
    }
}
