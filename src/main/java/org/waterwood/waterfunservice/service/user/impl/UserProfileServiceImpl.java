package org.waterwood.waterfunservice.service.user.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.api.BaseResponseCode;
import org.waterwood.common.exceptions.BusinessException;
import org.waterwood.waterfunservice.dto.request.user.UpdateUserProfileRequest;
import org.waterwood.waterfunservice.infrastructure.mapper.UserProfileMapper;
import org.waterwood.waterfunservicecore.api.PostPolicyDto;
import org.waterwood.waterfunservicecore.entity.user.User;
import org.waterwood.waterfunservicecore.entity.user.UserProfile;
import org.waterwood.common.exceptions.AuthException;
import org.waterwood.waterfunservicecore.infrastructure.persistence.user.UserProfileRepo;
import org.waterwood.waterfunservicecore.infrastructure.persistence.user.UserRepository;
import org.waterwood.waterfunservice.service.user.UserProfileService;
import org.waterwood.waterfunservicecore.infrastructure.security.AuthContextHelper;
import org.waterwood.waterfunservicecore.services.storage.CloudFileService;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private final UserProfileRepo upRepo;
    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    private final CloudFileService cloudFileService;

    public UserProfileServiceImpl(UserProfileRepo upRepo, UserRepository userRepository, UserProfileMapper userProfileMapper, CloudFileService cloudFileService) {
        this.upRepo = upRepo;
        this.userRepository = userRepository;
        this.userProfileMapper = userProfileMapper;
        this.cloudFileService = cloudFileService;
    }

    @Override
    public void addUserProfile(UserProfile up) {
        upRepo.save(up);
    }

    @Override
    @Transactional
    public void updateProfileByDto(UpdateUserProfileRequest dto) {
        User u = userRepository.findUserById(AuthContextHelper.getCurrentUserId()).orElseThrow(
                ()-> new BusinessException(BaseResponseCode.USER_NOT_FOUND)
        );
        UserProfile profile = upRepo.findUserProfileByUserId(u.getId()).orElseThrow(
                ()-> new BusinessException(BaseResponseCode.USER_NOT_FOUND)
        );
        userProfileMapper.toEntity(dto, profile);
        profile = upRepo.save(profile);
    }

    @Override
    public UserProfile getUserProfile(Long userId) {
        return upRepo.findUserProfileByUserId(userId).orElseThrow(
                ()-> new BusinessException(BaseResponseCode.USER_NOT_FOUND)
        );
    }

    @Override
    public UserProfile getUserProfile() {
        return upRepo.findUserProfileByUserId(AuthContextHelper.getCurrentUserId()).orElseThrow(()-> new AuthException(BaseResponseCode.USER_NOT_FOUND));
    }

    @Transactional
    @Override
    public PostPolicyDto getUploadPolicyAndSave(String fileSuffix) {
        PostPolicyDto dto =  cloudFileService.buildPutPolicy("avatar", fileSuffix);
        UserProfile userProfile = upRepo.findUserProfileByUserId(AuthContextHelper.getCurrentUserId())
                .orElseThrow(()-> new BusinessException(BaseResponseCode.USER_NOT_FOUND));
        cloudFileService.removeFile(userProfile.getAvatarUrl());
        userProfile.setAvatarUrl(dto.getKey());
        upRepo.save(userProfile);
        return dto;
    }
}
