package org.waterwood.waterfunservice.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.service.dto.OpResult;
import org.waterwood.waterfunservice.entity.user.AccountStatus;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.entity.permission.UserPermission;
import org.waterwood.waterfunservice.entity.permission.UserRole;
import org.waterwood.waterfunservice.repository.*;
import org.waterwood.waterfunservice.utils.PasswordUtil;
import org.waterwood.waterfunservice.utils.RepoUtil;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    @Autowired
    private RoleServiceImpl roleServiceImpl;
    @Autowired
    private PermissionServiceImpl permissionServiceImpl;

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
        return userPermRepo.findByUserId(userId).stream()
                .map(UserPermission::getPermission)
                .toList();
    }

    @Override
    public Set<Permission> getUserAllPermissions(long userId) {
        List<Role> roles = userRoleRepo.findByUserId(userId).stream().map(UserRole::getRole).toList();

        Set<Permission> rolePermission = roles.stream()
                .flatMap(role-> roleServiceImpl.getPermissions(role.getId()).stream())
                .collect(Collectors.toSet());

        Set<Permission> userPermission = userPermRepo.findByUserId(userId).stream()
                .map(UserPermission::getPermission).collect(Collectors.toSet());
        HashSet<Permission> permissions = new HashSet<>();
        permissions.addAll(rolePermission);
        permissions.addAll(userPermission);
        return permissions;
    }

    @Override
    public OpResult<Void> activateUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.ACTIVE);
    }

    @Override
    public OpResult<Void> deactivateUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.DEACTIVATED);
    }

    @Override
    public OpResult<Void> suspendUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.SUSPENDED);
    }

    @Override
    public OpResult<Void> deleteUser(long id) {
        return findUserAndUpdateStatus(id, AccountStatus.DELETED);

    }

    @Override
    public OpResult<Void> addUserRole(long userId, int roleId) {
        return RepoUtil.checkEntityExistsWithId(userRepository, userId, "User", ResponseCode.USER_NOT_FOUND, user ->
                RepoUtil.checkEntityExistsWithId(roleRepo, roleId, "Role", ResponseCode.ROLE_NOT_FOUND, role -> {
                    if (userRoleRepo.existsByUserIdAndRoleId(userId, roleId)) {
                        return OpResult.failure(ResponseCode.ROLE_ALREADY_EXISTS,
                                "User "+ userId +" already has role with ID " + roleId + ".");
                    }
                    UserRole userRole = new UserRole();
                    userRole.setUser(user);
                    userRole.setRole(role);
                    userRoleRepo.save(userRole);
                    return OpResult.success();
        }));
    }

    @Override
    public OpResult<Void> removeUserRole(long userId, int roleId) {
        return RepoUtil.checkEntityExistsWithId(userRepository, userId, "User", ResponseCode.USER_NOT_FOUND,
                user -> RepoUtil.checkEntityExistsWithId(roleRepo, roleId, "Role", ResponseCode.ROLE_NOT_FOUND,
                        role -> {
                    if (!userRoleRepo.existsByUserIdAndRoleId(userId, roleId)) {
                        return OpResult.failure(ResponseCode.ROLE_NOT_FOUND,
                                "User " + userId + " does not have role with ID " + roleId + ".");
                    }
                    userRoleRepo.deleteByUserIdAndRoleId(userId, roleId);
                    return OpResult.success();
                }));
    }

    @Override
    public OpResult<Void> addUserPermission(long userId, int permissionId) {
        return RepoUtil.checkEntityExistsWithId(userRepository, userId, "User", ResponseCode.USER_NOT_FOUND, user ->
                RepoUtil.checkEntityExistsWithId(permissionRepo,permissionId,"Permission", ResponseCode.PERMISSION_NOT_FOUND, permission -> {
                    if (userPermRepo.existsByUserIdAndPermissionId(userId, permissionId)) {
                        return OpResult.failure(ResponseCode.PERMISSION_ALREADY_EXISTS,
                                "User already has permission with ID " + permissionId + ".");
                    }
                    UserPermission userPermission = new UserPermission();
                    userPermission.setUser(user);
                    userPermission.setPermission(permission);
                    userPermRepo.save(userPermission);
                    return OpResult.success();
                }));
    }

    @Override
    public OpResult<Void> removeUserPermission(long userId, int permissionId) {
        return RepoUtil.checkEntityExistsWithId(userRepository, userId, "User", ResponseCode.USER_NOT_FOUND, user ->
                RepoUtil.checkEntityExistsWithId(permissionRepo, permissionId, "Permission", ResponseCode.PERMISSION_NOT_FOUND, permission -> {
                    if (!userPermRepo.existsByUserIdAndPermissionId(userId, permissionId)) {
                        return OpResult.failure(ResponseCode.PERMISSION_NOT_FOUND,
                                "User "+ userId +" does not have permission with ID " + permissionId + ".");
                    }
                    userPermRepo.deleteByUserIdAndPermissionId(userId, permissionId);
                    return OpResult.success();
        }));
    }

    private boolean checkPassword(String rawPassword, String hashedPassword) {
        return PasswordUtil.matchPassword(rawPassword, hashedPassword);
    }

    private OpResult<Void> findUserAndUpdateStatus(long userId, AccountStatus status) {
        return findUserAndUpdate(userId, user -> {
            user.setAccountStatus(status);
            user.setStatusChangedAt(Instant.now());
            user.setStatusChangeReason("Status changed to " + status.name());
        });
    }

    private OpResult<Void> findUserAndUpdate(long userId, Consumer<User> updater) {
        return userRepository.findById(userId).map(user -> {
            updater.accept(user);
            userRepository.save(user);
            return OpResult.success();
        }).orElse(
                OpResult.failure(ResponseCode.USER_NOT_FOUND, "User "+ userId + " does not exist.")
        );
    }
}
