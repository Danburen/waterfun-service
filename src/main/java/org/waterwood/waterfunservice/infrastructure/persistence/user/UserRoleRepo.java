package org.waterwood.waterfunservice.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.user.UserRole;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepo extends JpaRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
    List<UserRole> findByRoleId(Integer roleId);
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Integer roleId);
    void deleteByUserIdAndRoleId(Long userId, Integer roleId);
    boolean existsByUserIdAndRoleId(Long userId, Integer roleId);
}
