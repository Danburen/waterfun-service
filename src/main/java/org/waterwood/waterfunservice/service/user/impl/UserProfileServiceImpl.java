package org.waterwood.waterfunservice.service.user.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.entity.user.UserProfile;
import org.waterwood.waterfunservice.infrastructure.exception.AuthException;
import org.waterwood.waterfunservice.infrastructure.mapper.UserMapper;
import org.waterwood.waterfunservice.infrastructure.mapper.UserProfileMapper;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserProfileRepo;
import org.waterwood.waterfunservice.infrastructure.persistence.user.UserRepository;
import org.waterwood.waterfunservice.service.user.UserProfileService;
import org.waterwood.waterfunservice.infrastructure.utils.security.AuthContextHelper;
import org.waterwood.waterfunservice.infrastructure.security.RsaJwtUtil;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepo upRepo;
    private final RsaJwtUtil rsaJwtUtil;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;

    public UserProfileServiceImpl(UserProfileRepo upRepo, RsaJwtUtil rsaJwtUtil, UserMapper userMapper, UserRepository userRepository, UserProfileMapper userProfileMapper) {
        this.upRepo = upRepo;
        this.rsaJwtUtil = rsaJwtUtil;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public void addUserProfile(UserProfile up) {
        upRepo.save(up);
    }

    @Override
    public void updateProfile(UserProfile profile) {
        User u = userRepository.findUserById(AuthContextHelper.getCurrentUserId()).orElseThrow(
                ()-> new AuthException(ResponseCode.USER_NOT_FOUND)
        );
        profile.setUser(u);
        upRepo.save(profile);
    }

    @Override
    public UserProfile getUserProfile(Long userId) {
        return upRepo.findUserProfileByUserId(userId).orElseThrow(
                ()-> new AuthException(ResponseCode.USER_NOT_FOUND)
        );
    }

    @Override
    public UserProfile getUserProfile() {
        return upRepo.findUserProfileByUserId(AuthContextHelper.getCurrentUserId()).orElseThrow(()-> new AuthException(ResponseCode.USER_NOT_FOUND));
    }

    @Transactional
    @Override
    public void updateAvatar(String avatarUrl) {
        upRepo.updateAvatarUrl( AuthContextHelper.getCurrentUserId(), avatarUrl);
    }
}
