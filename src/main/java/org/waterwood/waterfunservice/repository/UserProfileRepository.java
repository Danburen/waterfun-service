package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.User.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

}
