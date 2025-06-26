package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.entity.permission.RolePermission;

import java.util.List;
import java.util.Optional;

public interface RolePermRepo extends JpaRepository<RolePermission,Integer> {
    List<RolePermission> findByRole(Role role);
    List<RolePermission> findByPermission(Permission permission);
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
}
