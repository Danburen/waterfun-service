package org.waterwood.waterfunservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.entity.permission.UserPermission;
import org.waterwood.waterfunservice.entity.permission.UserRole;
import org.waterwood.waterfunservice.repository.UserPermRepo;
import org.waterwood.waterfunservice.repository.UserRepository;
import org.waterwood.waterfunservice.repository.UserRoleRepo;
import org.waterwood.waterfunservice.service.user.RoleService;
import org.waterwood.waterfunservice.service.user.UserPermissionService;
import org.waterwood.waterfunservice.service.user.UserRoleService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserManagerService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRoleRepo userRoleRepo;
    @Autowired
    private UserPermRepo userPermRepo;
    @Autowired
    private UserPermissionService userPermissionService;
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private UserManagerService userManagerService;
    @Autowired
    private RoleService roleService;

    public List<Role> getUserRoles(long userId){
        return userRoleService.getUserRoles(userId);
    }

    public List<Permission> getUserPermissions(long userId){
        return userPermissionService.getUserPermissions(userId);
    }

    public Set<Permission> getUserAllPermissions(long userId) {
        List<Role> roles = userRoleRepo.findByUserId(userId).stream().map(UserRole::getRole).toList();

        Set<Permission> rolePermission = roles.stream()
                .flatMap(role-> roleService.getPermissions(role.getId()).stream())
                .collect(Collectors.toSet());

        Set<Permission> userPermission = userPermRepo.findByUserId(userId).stream()
                .map(UserPermission::getPermission).collect(Collectors.toSet());
        HashSet<Permission> permissions = new HashSet<>();
        permissions.addAll(rolePermission);
        permissions.addAll(userPermission);
        return permissions;
    }
}
