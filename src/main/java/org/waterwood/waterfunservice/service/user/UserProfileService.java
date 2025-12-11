package org.waterwood.waterfunservice.service.user;

import org.waterwood.waterfunservice.dto.request.user.UpdateUserProfileRequest;
import org.waterwood.waterfunservicecore.api.PostPolicyDto;
import org.waterwood.waterfunservicecore.api.resp.CloudResourcePresignedUrlResp;
import org.waterwood.waterfunservicecore.entity.user.UserProfile;

public interface UserProfileService {
    void addUserProfile(UserProfile up);

    /**
     * Update the User Profile
     * @param dto the DTO
     */
    void updateProfileByDto(UpdateUserProfileRequest dto);

    /**
     * Get the target User Profile
     *
     * @param userId the id of the target User
     * @return the entity
     */
    UserProfile getUserProfile(Long userId);

    /**
     * Get current User Profile
     * @return the entity
     */
    UserProfile getUserProfile();

    PostPolicyDto getUploadPolicyAndSaveAvatar(String avatarUrl);

    /**
     * Get the User Avatar
     * @return the presigned url
     */
    CloudResourcePresignedUrlResp getUserAvatar();
}
