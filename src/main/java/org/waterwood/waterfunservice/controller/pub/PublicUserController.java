package org.waterwood.waterfunservice.controller.pub;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.waterwood.api.ApiResponse;
import org.waterwood.waterfunservice.dto.response.UserPublicCardResp;
import org.waterwood.waterfunservice.dto.response.UserPublicInfoResp;
import org.waterwood.waterfunservice.dto.response.UserPublicProfileResp;
import org.waterwood.waterfunservice.service.user.UserService;
import org.waterwood.waterfunservicecore.infrastructure.mapper.UserMapper;

@RestController
@RequestMapping("/api/public/user")
@RequiredArgsConstructor
public class PublicUserController {
    private final UserMapper userMapper;
    private UserService userService;
    @RequestMapping("/{userUid}/info")
    public ApiResponse<UserPublicInfoResp> getUserInfo(@PathVariable long userUid) {
        return ApiResponse.success(
                userService.getPublicUserInfo(userUid)
        );
    }


    @RequestMapping("/{userUid}/profile")
    public ApiResponse<UserPublicProfileResp> getUserProfile(@PathVariable long userUid) {
        return ApiResponse.success(
                userService.getPublicUserProfile(userUid)
        );
    }

    @RequestMapping("/{userUid}/card")
    public ApiResponse<UserPublicCardResp> getUserCard(@PathVariable long userUid) {
        return ApiResponse.success(
                userService.getPublicUserCard(userUid)
        );
    }

    @RequestMapping("/{userUid}/follower")
    public ApiResponse<Page<UserPublicCardResp>> getFollower(@PathVariable long userUid,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ApiResponse.success(
                userService.listUserFollowers(userUid, pageable)
        );
    }

    @RequestMapping("/{userUid}/following")
    public ApiResponse<Page<UserPublicCardResp>> getFollowing(@PathVariable long userUid,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return ApiResponse.success(
                userService.listUserFollowing(userUid, pageable)
        );
    }
}
