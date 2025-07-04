package org.waterwood.waterfunservice.service.user;

import org.waterwood.waterfunservice.service.dto.OpResult;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserById(long id);
    List<User> getUserByRole(String role);

    List<Role> getUserRoles(long userId);
    List<Permission> getUserPermissions(long userId);

    Set<Permission> getUserAllPermissions(long userId);

    OpResult<Void> activateUser(long id);
    OpResult<Void> deactivateUser(long id);
    OpResult<Void> suspendUser(long id);
    OpResult<Void> deleteUser(long id);

    /**
     * Add or remove user role or permission
     * @param userId the user id
     * @param roleId the role id
     * @return OperationResult indicating success or failure
     */
    OpResult<Void> addUserRole(long userId, int roleId);

    /**
     * Remove a role from a user
     * @param userId the user id
     * @param roleId the role id
     * @return OperationResult indicating success or failure
     */
    OpResult<Void> removeUserRole(long userId, int roleId);

    /**
     * Add or remove user permission
     * @param userId the user id
     * @param permissionId the permission id
     * @return OperationResult indicating success or failure
     */
    OpResult<Void> addUserPermission(long userId, int permissionId);

    /**
     * Remove a permission from a user
     * @param userId the user id
     * @param permissionId the permission id
     * @return OperationResult indicating success or failure
     */
    OpResult<Void> removeUserPermission(long userId, int permissionId);
}
