package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.permission.UserPermission;

import java.util.List;

public interface UserPermRepo extends JpaRepository<UserPermission,Long> {
    List<UserPermission> findByUserId(Long userId);
}
