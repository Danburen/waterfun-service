package org.waterwood.waterfunservice.service;

import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.response.UserInfo;
import org.waterwood.waterfunservice.DTO.response.UserProfile;
import org.waterwood.waterfunservice.repository.UserProfileRepo;
import org.waterwood.waterfunservice.repository.UserRepository;

@Service
public class AccountService {
    private final UserRepository userRepository;
    private final UserProfileRepo userProfileRepo;

    public AccountService(UserRepository userRepository, UserProfileRepo userProfileRepo) {
        this.userRepository = userRepository;
        this.userProfileRepo = userProfileRepo;
    }

    public ApiResponse<UserInfo> getUserInfo(Long userId) {
        if(userId==null) return ResponseCode.USER_NOT_FOUND.toApiResponse();
        return userRepository.findById(userId).map(user->{
            String username = user.getUsername();
            return ApiResponse.success( userProfileRepo.findById(userId).map(userProfile -> {
                String avatarUrl = userProfile.getAvatarUrl();
                String nickname = userProfile.getNickname();
                return new UserInfo(userId, username, avatarUrl, nickname);
            }).orElse(new UserInfo(userId, username, null, null)));
        }).orElse(ResponseCode.NOT_FOUND.toApiResponse());
    }
}
