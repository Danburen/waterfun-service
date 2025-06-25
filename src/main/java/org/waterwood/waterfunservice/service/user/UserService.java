package org.waterwood.waterfunservice.service.user;

import org.waterwood.waterfunservice.entity.User.User;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(long id);
    List<User> getUserByRole(String role);

    List<Role> getUserRoles(long userId);
    List<Permission> getUserPermissions(long userId);

    void activateUser(long id);
    void deactivateUser(long id);
    void suspendUser(long id);
    void deleteUser(long id);

    void addUserRole(long userId, int roleId);
    void removeUserRole(long userId, int roleId);
    void addUserPermission(long userId,int permissionId);
    void removeUserPermission(long userId,int permissionId);
}
