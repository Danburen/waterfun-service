package org.waterwood.waterfunservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.Permission;
import org.waterwood.waterfunservice.entity.Role;
import org.waterwood.waterfunservice.entity.RolePermission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RolePermRepo extends JpaRepository<RolePermission,Integer> {
    List<RolePermission> findByRole(Role role);
    List<RolePermission> findByPermission(Permission permission);
    Optional<RolePermission> findByRoleAndPermission(Role role, Permission permission);
    int deleteByRoleId(int id);

    void deleteByRoleIdAndPermissionIn(Integer roleId, Collection<Permission> permissions);

    Optional<Object> findByRoleIdAndPermissionId(int roleId, int permissionId);

    void deleteByRoleIdAndPermissionIdIn(Integer roleId, Collection<Integer> permissionIds);
}
