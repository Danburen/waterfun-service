package org.waterwood.waterfunservice.service;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.result.OpResult;
import org.waterwood.waterfunservice.entity.permission.Permission;
import org.waterwood.waterfunservice.entity.permission.PermissionType;
import org.waterwood.waterfunservice.repository.PermissionRepo;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class PermissionService {
    @Autowired
    PermissionRepo permissionRepo;

    /* Create  */
    @Transactional
    public OpResult<Void> addPermission(String code, String name, String description, PermissionType type, String resource) {
        if (isPermissionExists(code)) {
            return OpResult.failure("Permission "+ name +"("+ code +") already exists.");
        }
        Permission permission = new Permission();
        permission.setCode(code);
        permission.setName(name);
        permission.setDescription(description);
        permission.setType(type);
        permission.setResource(resource);

        permissionRepo.save(permission);
        return OpResult.success();
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
    public OpResult<Void> updatePermCode(int permId,String code){
        if(isPermissionExists(code)){
            return OpResult.failure("Permission "+ code +"("+ permId +") already exists.");
        }
        return ifPermExistsThen(permId,perm->perm.setCode(code));
    }

    @Transactional
    public OpResult<Void> updatePermName(int permId,String name){
        return ifPermExistsThen(permId,perm->perm.setName(name));
    }

    @Transactional
    public OpResult<Void> updatePermDescription(int permId,String description){
        return ifPermExistsThen(permId,perm->perm.setDescription(description));
    }

    @Transactional
    public OpResult<Void> updatePermType(int permId,PermissionType type){
        return ifPermExistsThen(permId,perm->perm.setType(type));
    }

    @Transactional
    public OpResult<Void> updatePermResource(int permId,String resource){
        return ifPermExistsThen(permId,perm->perm.setResource(resource));
    }

    /* Delete */

    @Transactional
    public OpResult<Void> deletePermCode(int permId){
        return ifPermExistsThen(permId,perm->permissionRepo.deleteById(permId));
    }

    @Transactional
    public OpResult<Void> ifPermExistsThen(int permId, Consumer<Permission> permConsumer){
        return permissionRepo.findById(permId).map(perm->{
            permConsumer.accept(perm);
            return OpResult.success();
        }).orElse(OpResult.failure(ResponseCode.PERMISSION_NOT_FOUND,"Permission "+ permConsumer +" doesn't exists"));
    }

    @Transactional
    public OpResult<Void> ifPermNotExistsThen(int permId, Supplier<OpResult<Void>> operation) {
        if (permissionRepo.existsById(permId)) {
            Permission perm = permissionRepo.findById(permId).orElseThrow();
            return OpResult.failure(ResponseCode.PERMISSION_NOT_FOUND,
                    "Permission " + perm.getName() +" ("+ perm.getId() + ") already exists");
        }
        return operation.get();
    }
}
