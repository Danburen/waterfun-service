package org.waterwood.waterfunservice.service.user;

import org.waterwood.waterfunservice.DTO.common.result.OperationResult;
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

    OperationResult<Void> activateUser(long id);
    OperationResult<Void> deactivateUser(long id);
    OperationResult<Void> suspendUser(long id);
    OperationResult<Void> deleteUser(long id);

    /**
     * Add or remove user role or permission
     * @param userId the user id
     * @param roleId the role id
     * @return OperationResult indicating success or failure
     */
    OperationResult<Void> addUserRole(long userId, int roleId);

    /**
     * Remove a role from a user
     * @param userId the user id
     * @param roleId the role id
     * @return OperationResult indicating success or failure
     */
    OperationResult<Void> removeUserRole(long userId, int roleId);

    /**
     * Add or remove user permission
     * @param userId the user id
     * @param permissionId the permission id
     * @return OperationResult indicating success or failure
     */
    OperationResult<Void> addUserPermission(long userId,int permissionId);

    /**
     * Remove a permission from a user
     * @param userId the user id
     * @param permissionId the permission id
     * @return OperationResult indicating success or failure
     */
    OperationResult<Void> removeUserPermission(long userId,int permissionId);
}
