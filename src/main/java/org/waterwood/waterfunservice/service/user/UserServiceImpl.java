package org.waterwood.waterfunservice.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ErrorType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.result.OperationResult;
import org.waterwood.waterfunservice.entity.User.AccountStatus;
import org.waterwood.waterfunservice.entity.User.User;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.entity.permission.UserPermission;
import org.waterwood.waterfunservice.entity.permission.UserRole;
import org.waterwood.waterfunservice.repository.*;
import org.waterwood.waterfunservice.utils.PasswordUtil;
import org.waterwood.waterfunservice.utils.RepoUtil;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    private UserPermRepo userPermRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private PermissionRepo permissionRepo;

    @Override
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> getUserByRole(String role) {
        return List.of();
    }

    @Override
    public List<Role> getUserRoles(long userId) {
        return userRoleRepo.findByUserId(userId)
                .stream()
                .map(UserRole::getRole)
                .toList();
    }

    @Override
    public List<Permission> getUserPermissions(long userId) {
        return userPermRepo.findByUserId(userId)
                .stream()
                .map(UserPermission::getPermission)
                .toList();
    }

    @Override
    public OperationResult<Void> activateUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.ACTIVE);
    }

    @Override
    public OperationResult<Void> deactivateUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.DEACTIVATED);
    }

    @Override
    public OperationResult<Void> suspendUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.SUSPENDED);
    }

    @Override
    public OperationResult<Void> deleteUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.DELETED);

    }

    @Override
    public OperationResult<Void> addUserRole(long userId, int roleId) {
        return RepoUtil.checkEntityExists(userRepository, userId, "User", ResponseCode.USER_NOT_FOUND, user ->
                RepoUtil.checkEntityExists(roleRepo, roleId, "Role", ResponseCode.ROLE_NOT_FOUND, role -> {
                    if (userRoleRepo.existsByUserIdAndRoleId(userId, roleId)) {
                        return OperationResult.<Void>builder()
                                .trySuccess(false)
                                .errorType(ErrorType.CLIENT)
                                .responseCode(ResponseCode.ROLE_ALREADY_EXISTS)
                                .message("User already has role with ID " + roleId + ".")
                                .build();
                    }
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRoleRepo.save(userRole);
                    return OperationResult.<Void>builder()
                            .trySuccess(true)
                            .responseCode(ResponseCode.OK)
                            .build();
        }));
    }

    @Override
    public OperationResult<Void> removeUserRole(long userId, int roleId) {
        return RepoUtil.checkEntityExists(userRepository, userId, "User", ResponseCode.USER_NOT_FOUND,
                user -> RepoUtil.checkEntityExists(roleRepo, roleId, "Role", ResponseCode.ROLE_NOT_FOUND,
                        role -> {
                    if (!userRoleRepo.existsByUserIdAndRoleId(userId, roleId)) {
                        return OperationResult.<Void>builder()
                                .trySuccess(false)
                                .errorType(ErrorType.CLIENT)
                                .responseCode(ResponseCode.ROLE_NOT_FOUND)
                                .message("User does not have role with ID " + roleId + ".")
                                .build();
                    }
                    userRoleRepo.deleteByUserIdAndRoleId(userId, roleId);
                    return OperationResult.<Void>builder()
                            .trySuccess(true)
                            .responseCode(ResponseCode.OK)
                            .build();
                }));
    }

    @Override
    public OperationResult<Void> addUserPermission(long userId, int permissionId) {
        return RepoUtil.checkEntityExists(userRepository, userId, "User", ResponseCode.USER_NOT_FOUND, user ->
                RepoUtil.checkEntityExists(permissionRepo,permissionId,"Permission", ResponseCode.PERMISSION_NOT_FOUND, permission -> {
                    if (userPermRepo.existsByUserIdAndPermissionId(userId, permissionId)) {
                        return OperationResult.<Void>builder()
                                .trySuccess(false)
                                .errorType(ErrorType.CLIENT)
                                .responseCode(ResponseCode.PERMISSION_ALREADY_EXISTS)
                                .message("User already has permission with ID " + permissionId + ".")
                                .build();
                    }
                    UserPermission userPermission = new UserPermission();
                    userPermission.setUser(user);
                    userPermission.setPermission(permission);
                    userPermRepo.save(userPermission);
                    return OperationResult.<Void>builder()
                            .trySuccess(true)
                            .responseCode(ResponseCode.OK)
                            .build();
                }));
    }

    @Override
    public OperationResult<Void> removeUserPermission(long userId, int permissionId) {
        return RepoUtil.checkEntityExists(userRepository, userId, "User", ResponseCode.USER_NOT_FOUND, user ->
                RepoUtil.checkEntityExists(permissionRepo, permissionId, "Permission", ResponseCode.PERMISSION_NOT_FOUND, permission -> {
                    if (!userPermRepo.existsByUserIdAndPermissionId(userId, permissionId)) {
                        return OperationResult.<Void>builder()
                                .trySuccess(false)
                                .errorType(ErrorType.CLIENT)
                                .responseCode(ResponseCode.PERMISSION_NOT_FOUND)
                                .message("User does not have permission with ID " + permissionId + ".")
                                .build();
                    }
                    userPermRepo.deleteByUserIdAndPermissionId(userId, permissionId);
                    return OperationResult.<Void>builder()
                            .trySuccess(true)
                            .responseCode(ResponseCode.OK)
                            .build();
        }));
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return PasswordUtil.matchPassword(rawPassword, hashedPassword);
    }

    private OperationResult<Void> findUserAndUpdateStatus(long userId, AccountStatus status) {
        return findUserAndUpdate(userId, user -> {
            user.setAccountStatus(status);
            user.setStatusChangedAt(Instant.now());
            user.setStatusChangeReason("Status changed to " + status.name());
        });
    }

    private OperationResult<Void> findUserAndUpdate(long userId, Consumer<User> updater) {
        return userRepository.findById(userId).map(user -> {
            updater.accept(user);
            userRepository.save(user);
            return OperationResult.<Void>builder()
                    .trySuccess(true)
                    .responseCode(ResponseCode.OK)
                    .build();
        }).orElse(
                OperationResult.<Void>builder()
                        .trySuccess(false)
                        .errorType(ErrorType.CLIENT)
                        .responseCode(ResponseCode.USER_NOT_FOUND)
                        .message("User with ID " + userId + " not found.")
                        .build()
        );
    }
}
