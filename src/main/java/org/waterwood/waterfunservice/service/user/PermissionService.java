package org.waterwood.waterfunservice.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.PermissionType;
import org.waterwood.waterfunservice.repository.PermissionRepo;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class PermissionService {
    @Autowired
    PermissionRepo permissionRepo;
    /* Create  */
    @Transactional
    public ApiResponse<Void> addPermission(String code, String name, String description, PermissionType type, String resource) {
        if (isPermissionExists(code)) return ApiResponse.failure(ResponseCode.PERMISSION_ALREADY_EXISTS);
        Permission permission = new Permission();
        permission.setCode(code);
        permission.setName(name);
        permission.setDescription(description);
        permission.setType(type);
        permission.setResource(resource);

        permissionRepo.save(permission);
        return ApiResponse.accept();
    }

    /* Read  */

    @Transactional(readOnly = true)
    public boolean isPermissionExists(String code) {
        return permissionRepo.findByCode(code).isPresent();
    }

    @Transactional(readOnly = true)
    public Optional<Permission> getPermission(String code) {
        return permissionRepo.findByCode(code);
    }

    @Transactional(readOnly = true)
    public Optional<Permission> getPermission(int PermId){
        return permissionRepo.findById(PermId);
    }

    @Transactional(readOnly = true)
    public Page<Permission> getPermissions(Pageable pageable) {
        return permissionRepo.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<Permission> getPermsByName(String name){
        return permissionRepo.findByName(name);
    }
    /* Update */

    @Transactional
    public ApiResponse<Void> updatePermCode(int permId,String code){
        if(isPermissionExists(code)){
            return ApiResponse.failure("Permission "+ code +"("+ permId +") already exists.");
        }
        return ifPermExists(permId, perm->perm.setCode(code));
    }

    @Transactional
    public ApiResponse<Void> updatePermName(int permId,String name){
        return ifPermExists(permId, perm->perm.setName(name));
    }

    @Transactional
    public ApiResponse<Void> updatePermDescription(int permId,String description){
        return ifPermExists(permId, perm->perm.setDescription(description));
    }

    @Transactional
    public ApiResponse<Void> updatePermType(int permId,PermissionType type){
        return ifPermExists(permId, perm->perm.setType(type));
    }

    @Transactional
    public ApiResponse<Void> updatePermResource(int permId,String resource){
        return ifPermExists(permId, perm->perm.setResource(resource));
    }

    /* Delete */

    @Transactional
    public ApiResponse<Void> deletePermCode(int permId){
        return ifPermExists(permId,
                perm->permissionRepo.delete(perm));
    }

    @Transactional
    public ApiResponse<Void> ifPermExists(int permId, Consumer<Permission> action) {
        return permissionRepo.findById(permId)
                .map(perm -> {
                    action.accept(perm);
                    return ApiResponse.accept();
                })
                .orElse(ApiResponse.failure(
                        ResponseCode.PERMISSION_NOT_FOUND,
                        "Permission " + permId + " doesn't exist"));
    }
}
