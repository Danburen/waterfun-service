package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.user.UserProfile;

public interface UserProfileRepo extends JpaRepository<UserProfile, Long> {

}
