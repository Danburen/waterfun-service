package org.waterwood.waterfunservice.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservicecore.api.req.user.UpdateUserProfileRequest;
import org.waterwood.api.ApiResponse;
import org.waterwood.waterfunservicecore.api.resp.user.UserInfoResponse;
import org.waterwood.waterfunservicecore.api.resp.user.UserProfileResponse;
import org.waterwood.waterfunservicecore.api.resp.PostPolicyResp;
import org.waterwood.waterfunservicecore.api.resp.CloudResPresignedUrlResp;
import org.waterwood.waterfunservicecore.entity.Permission;
import org.waterwood.waterfunservicecore.infrastructure.mapper.UserCoreMapper;
import org.waterwood.waterfunservicecore.infrastructure.mapper.UserProfileCoreMapper;
import org.waterwood.waterfunservicecore.entity.user.User;
import org.waterwood.waterfunservicecore.entity.user.UserProfile;
import org.waterwood.waterfunservicecore.infrastructure.security.AuthContextHelper;
import org.waterwood.waterfunservicecore.infrastructure.utils.context.UserContext;
import org.waterwood.waterfunservicecore.services.user.UserProfileCoreServiceImpl;
import org.waterwood.waterfunservicecore.services.user.UserCoreService;
import org.waterwood.waterfunservicecore.services.sys.storage.CloudFileService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserCoreService userCoreService;
    private final UserProfileCoreServiceImpl userProfileService;
    private final UserCoreMapper userCoreMapper;
    private final UserProfileCoreMapper userProfileCoreMapper;
    private final CloudFileService cloudFileService;

    @GetMapping("/userInfo")
    public ApiResponse<UserInfoResponse> getUserInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal UserContext ctx){
        User user = userCoreService.getUserByUid(ctx.getUserUid());
        UserInfoResponse res = userCoreMapper.toUserInfoResponse(user);
        res.setAvatar(userProfileService.getUserAvatar(user.getUid()));
        res.setPasswordHash(user.getPasswordHash() != null);
        return ApiResponse.success(res);
    }
    @PutMapping("/updateProfile")
    public ApiResponse<Void> updateProfile(@RequestBody @Valid UpdateUserProfileRequest body,
                                           @Parameter(hidden = true) @AuthenticationPrincipal UserContext ctx){
        userProfileService.updateProfileByDto(ctx.getUserUid(), body);
        return ApiResponse.success();
    }

    @GetMapping("/profile")
    public ApiResponse<UserProfileResponse> getProfile(@Parameter(hidden = true) @AuthenticationPrincipal UserContext ctx){
        UserProfile up = userProfileService.getUserProfile(ctx.getUserUid());
        UserProfileResponse res = userProfileCoreMapper.toResponse(up);
        return ApiResponse.success(res);
    }


    @GetMapping("/avatar/upload")
    public ApiResponse<PostPolicyResp> updateAvatar(@RequestParam String suffix,
                                                    @Parameter(hidden = true) @AuthenticationPrincipal UserContext ctx){
        return ApiResponse.success(userProfileService.getUploadPolicyAndSaveAvatar(ctx.getUserUid(), suffix));
    }

    @GetMapping("/avatar")
    public ApiResponse<CloudResPresignedUrlResp> getAvatar(@Parameter(hidden = true) @AuthenticationPrincipal UserContext ctx){
        return ApiResponse.success(
                userProfileService.getUserAvatar(ctx.getUserUid())
        );
    }


    @GetMapping("/permissions")
    public ApiResponse<Set<String>> getPermissions(){
        long userUid = AuthContextHelper.getCurrentUserUid();
        Set<String> permCodes = userCoreService.getUserPermissions(userUid)
                .stream().map(Permission::getCode)
                .collect(Collectors.toSet());
        return ApiResponse.success(permCodes);
    }
}
