package org.waterwood.waterfunservice.service.user;

import org.waterwood.waterfunservice.entity.user.UserProfile;

public interface UserProfileService {
    void addUserProfile(UserProfile up);

    /**
     * Update the User Profile
     * @param profile the request body
     */
    void updateProfile(UserProfile profile);

    /**
     * Get the target User Profile
     *
     * @param userId the id of the target User
     * @return the entity of{@link UserProfile}
     */
    UserProfile getUserProfile(Long userId);

    /**
     * Get current User Profile
     * @return the entity of{@link UserProfile}
     */
    UserProfile getUserProfile();

    void updateAvatar(String avatarUrl);
}
