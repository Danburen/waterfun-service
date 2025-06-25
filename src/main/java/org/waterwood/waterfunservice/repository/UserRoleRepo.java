package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.permission.UserRole;

import java.util.List;

public interface UserRoleRepo extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
}
