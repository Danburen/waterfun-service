package org.waterwood.waterfunservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.dto.common.enums.PermissionType;
import org.waterwood.waterfunservice.dto.request.perm.CreatePermRequest;
import org.waterwood.waterfunservice.dto.request.perm.updatePermRequest;
import org.waterwood.waterfunservice.dto.response.comm.ApiResponse;
import org.waterwood.waterfunservice.dto.response.PermissionResp;
import org.waterwood.waterfunservice.dto.request.perm.PatchPermRequest;
import org.waterwood.waterfunservice.entity.Permission;
import org.waterwood.waterfunservice.infrastructure.mapper.PermissionMapper;
import org.waterwood.waterfunservice.infrastructure.persistence.utils.PermSpec;
import org.waterwood.waterfunservice.service.perm.PermissionService;
import org.waterwood.waterfunservice.service.user.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permission")
public class PermissionController {
    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    @GetMapping("/list")
    public ApiResponse<Page<PermissionResp>> listUserPermissions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) PermissionType  type,
            @RequestParam(required = false) String resource,
            @RequestParam(required = false) Integer parentId){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Specification<Permission> spec = PermSpec.of(name, type, resource, parentId);
        Page<PermissionResp> perms = permissionService.listPermissions(spec, pageable)
                .map(permissionMapper::toPermissionResp);
        return ApiResponse.success(perms);
    }

    @PutMapping
    public ApiResponse<Void> AddPermission(@RequestBody @Valid CreatePermRequest  body){
        permissionService.addPermission(permissionMapper.toEntity(body));
        return ApiResponse.success();
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> partialUpdatePermission(@PathVariable int id, @RequestBody @Valid PatchPermRequest body){
        Permission perm = permissionMapper
                .partialUpdate(body, permissionService.getPermission(id));
        permissionService.update(perm);
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> UpdatePermission(@PathVariable int id, @RequestBody @Valid updatePermRequest body){
        Permission perm = permissionMapper
                .fullUpdate(body, permissionService.getPermission(id));
        permissionService.update(perm);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> DeletePermission(@PathVariable String id){
        permissionService.deleteUser(Integer.parseInt(id));
        return ApiResponse.success();
    }
}
