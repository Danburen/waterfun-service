package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.permission.Permission;

public interface PermissionRepo extends JpaRepository<Permission, Long> {
}
