package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.User.UserLevelMapping;

public interface UserLevelMappingRepository extends JpaRepository<UserLevelMapping, Long> {
}
