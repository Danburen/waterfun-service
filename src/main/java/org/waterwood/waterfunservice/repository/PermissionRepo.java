package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.UserPermission;
import org.waterwood.waterfunservice.entity.user.User;

import java.util.List;
import java.util.Optional;

public interface PermissionRepo extends JpaRepository<Permission, Integer> {
    List<Permission> findByName(String name);
    Optional<Permission> findByCode(String code);

    List<Permission> getPermissionById(Integer id);
}
