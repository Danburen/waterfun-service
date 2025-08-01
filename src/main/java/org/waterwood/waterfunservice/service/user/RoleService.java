package org.waterwood.waterfunservice.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.Role;
import org.waterwood.waterfunservice.entity.permission.RolePermission;
import org.waterwood.waterfunservice.repository.RolePermRepo;
import org.waterwood.waterfunservice.repository.RoleRepo;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class RoleService {
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    RolePermRepo rolePermRepo;
    /* Create */
    @Transactional
    public ApiResponse<Void> addRole(String roleName) {
        if(isRoleExists(roleName)) {
            return ApiResponse.failure(ResponseCode.ROLE_ALREADY_EXISTS, "Role already exists: " + roleName);
        }
        Role role = new Role();
        role.setName(roleName);
        return ApiResponse.accept();
    }

    @Transactional
    public ApiResponse<Void> addRole(String roleName, String description) {
        if(isRoleExists(roleName)) {
            return ApiResponse.failure(ResponseCode.ROLE_ALREADY_EXISTS, "Role already exists: " + roleName);
        }
        Role role = new Role();
        role.setName(roleName);
        role.setDescription(description);
        return ApiResponse.accept();
    }

    @Transactional
    public ApiResponse<Void> addRole(String roleName,String description,int parentId){
        if(isRoleExists(roleName)) {
            return ApiResponse.failure(ResponseCode.ROLE_ALREADY_EXISTS, "Role already exists: " + roleName);
        }
        return roleRepo.findById(parentId).map(parentRole -> {
            Role role = new Role();
            role.setName(roleName);
            role.setDescription(description);
            role.setParent(parentRole);
            return ApiResponse.accept();
        }).orElse(
                ApiResponse.failure(ResponseCode.ROLE_NOT_FOUND, "Parent role not found: " + parentId)
        );
    }

    /* Read */
    @Transactional(readOnly = true)
    public boolean isRoleExists(String roleName) {
        return roleRepo.findByName(roleName).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean isRoleExists(int roleId) {
        return roleRepo.existsById(roleId);
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRole(int roleId) {
        return roleRepo.findById(roleId);
    }

    @Transactional(readOnly = true)
    public Optional<Role> getRole(String roleName) {
        return roleRepo.findByName(roleName);
    }

    @Transactional(readOnly = true)
    public Page<Role> getRoles(Pageable pageable) {
        return roleRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermissions(int roleId){
        return roleRepo.findById(roleId).map(role ->
                        rolePermRepo.findByRole(role).stream()
                        .map(RolePermission::getPermission)
                        .toList())
                .orElse(List.of());
    }

    /* Delete */
    @Transactional
    public ApiResponse<Void> deleteRole(int roleId) {
        return ifRoleExistsDo(roleId, rc->{
            roleRepo.delete(rc);
        });
    }

    /* Update */

    @Transactional
    public ApiResponse<Void> updateRoleName(int roleId, String newRoleName) {
        return ifRoleExistsDo(roleId, role->{
            role.setName(newRoleName);
        });
    }

    @Transactional
    public ApiResponse<Void> updateRoleDescription(int roleId, String newRoleDescription) {
        return ifRoleExistsDo(roleId, role->{
            role.setDescription(newRoleDescription);
            roleRepo.save(role);
        });
    }

    @Transactional
    public ApiResponse<Void> updateRoleParent(int roleId, int parentId) {
        if(roleId == parentId) {
            return ApiResponse.failure(ResponseCode.REDUNDANT_OPERATION,
                    "Role cannot be its own parent: " + roleId);
        }
        return roleRepo.findById(parentId).map(parentRole->
                roleRepo.findById(roleId).map(role -> {
                    role.setParent(parentRole);
                    return ApiResponse.accept();
                }).orElse(ApiResponse.failure(ResponseCode.ROLE_NOT_FOUND, "Role not found: " + roleId))
        ).orElse(ApiResponse.failure(ResponseCode.ROLE_NOT_FOUND, "Parent role not found: " + parentId));
    }

    @Transactional
    public ApiResponse<Void> ifRoleExistsDo(int roleId, Consumer<Role> roleConsumer) {
        return roleRepo.findById(roleId).map(
                role -> {
                    roleConsumer.accept(role);
                    return ApiResponse.accept();
                }
        ).orElse(
                ApiResponse.failure(ResponseCode.ROLE_NOT_FOUND, "Role not found: " + roleId)
        );
    }
}