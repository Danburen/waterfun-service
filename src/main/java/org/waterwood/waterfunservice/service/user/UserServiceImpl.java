package org.waterwood.waterfunservice.service.user;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.waterwood.api.BaseResponseCode;
import org.waterwood.common.exceptions.BusinessException;
import org.waterwood.waterfunservice.dto.response.UserPublicCardResp;
import org.waterwood.waterfunservice.dto.response.UserPublicInfoResp;
import org.waterwood.waterfunservice.dto.response.UserPublicProfileResp;
import org.waterwood.waterfunservicecore.entity.user.*;
import org.waterwood.waterfunservicecore.infrastructure.persistence.user.UserFollowerRepository;
import org.waterwood.waterfunservicecore.services.user.UserCoreProfileService;
import org.waterwood.waterfunservicecore.services.user.UserCoreService;
import org.waterwood.waterfunservicecore.services.user.UserCounterCoreService;
import org.waterwood.waterfunservicecore.services.user.UserDatumCoreService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserDatumCoreService userDatumCoreService;
    private final UserCoreProfileService userCoreProfileService;
    private final UserCounterCoreService userCounterCoreService;
    private final UserCoreService userCoreService;
    private final UserFollowerRepository userFollowerRepository;

    @Operation(summary = "获取某个用户的公开基本信息")
    @Override
    public UserPublicInfoResp getPublicUserInfo(long userUid) {
        User u = userCoreService.getUser(userUid);
        if(! userCounterCoreService.isVisible(userUid)){
            throw new BusinessException(
                    BaseResponseCode.NOT_FOUND
            );
        }
        return new UserPublicInfoResp(
                u.getUid(),
                u.getUsername(),
                userCoreProfileService.getUserAvatar(userUid),
                u.getCreatedAt(),
                u.getLastActiveAt()
        );
    }

    @Operation(summary = "获取某个用户的公开资料")
    @Override
    public UserPublicProfileResp getPublicUserProfile(long userUid) {
        User u = userCoreService.getUser(userUid);
        UserProfile up = userCoreProfileService.getUserProfile(userUid);
        UserCounter uc = userCounterCoreService.getUserCounter(userUid);
        if(!(uc.getVisible() == 1)) throw new BusinessException(
                BaseResponseCode.NOT_FOUND
        );
        return new UserPublicProfileResp(
                        up.getGender(),
                        uc.getLevel(),
                        uc.getExp(),
                        uc.getFollowerCnt(),
                        uc.getFollowingCnt(),
                        uc.getPostCnt(),
                        uc.getLikeCnt(),
                        u.getCreatedAt()
                );
    }

    @Operation(summary = "获取某个用户的公开卡片")
    @Override
    public UserPublicCardResp getPublicUserCard(long userUid) {
        User u = userCoreService.getUser(userUid);
        UserProfile up = userCoreProfileService.getUserProfile(userUid);
        UserCounter uc = userCounterCoreService.getUserCounter(userUid);
        return uc.isVisible() ? new UserPublicCardResp(
                u.getUid(),
                u.getUsername(),
                userCoreProfileService.getUserAvatar(userUid),
                up.getNickname(),
                uc.getLevel(),
                uc.getVisible()
        ) : new UserPublicCardResp(
                null,
                null,
                null,
                null,
                null,
                uc.getVisible()
        );
    }

    @Operation(summary = "获取某个用户的粉丝列表")
    @Override
    public Page<UserPublicCardResp> listUserFollowers(long spec, Pageable pageable) {
        return userFollowerRepository.fetchFollowers(spec, pageable).map(
                uf -> new UserPublicCardResp(
                        uf.getFollower().getUid(),
                        uf.getUser().getUsername(),
                        userCoreProfileService.getUserAvatar(uf.getFollower().getUid()),
                        uf.getFollower().getUsername(),
                        uf.getCounter().getLevel(),
                        uf.getCounter().getVisible()
                ));
    }

    @Operation(summary = "获取某个用户的关注列表")
    @Override
    public Page<UserPublicCardResp> listUserFollowing(long userUid, Pageable pageable) {
        return userFollowerRepository.fetchFollowings(userUid, pageable).map(
                uf -> new UserPublicCardResp(
                        uf.getFollower().getUid(),
                        uf.getUser().getUsername(),
                        userCoreProfileService.getUserAvatar(uf.getFollower().getUid()),
                        uf.getFollower().getUsername(),
                        uf.getCounter().getLevel(),
                        uf.getCounter().getVisible()
                )
        );
    }
}
