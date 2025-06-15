package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.User.UserLevel;

public interface UserLevelRepository extends JpaRepository<UserLevel, Integer> {
}
