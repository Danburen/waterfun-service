package org.waterwood.waterfunservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.permission.UserPermission;

import java.util.List;
import java.util.Optional;

public interface UserPermRepo extends JpaRepository<UserPermission,Long> {
    List<UserPermission> findByUserId(Long userId);
    List<UserPermission> findByPermissionId(Integer permissionId);
    Optional<UserPermission> findByUserIdAndPermissionId(Long userId, Integer permissionId);
    void deleteByUserIdAndPermissionId(Long userId, Integer permissionId);
    boolean existsByUserIdAndPermissionId(Long userId, Integer permissionId);
}
