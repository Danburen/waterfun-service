package org.waterwood.waterfunservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.waterwood.waterfunservice.dto.request.role.CreateRoleRequest;
import org.waterwood.waterfunservice.dto.request.role.UpdateRoleRequest;
import org.waterwood.waterfunservice.dto.response.comm.ApiResponse;
import org.waterwood.waterfunservice.entity.PatchRoleRequest;
import org.waterwood.waterfunservice.entity.Role;
import org.waterwood.waterfunservice.dto.response.role.RoleResp;
import org.waterwood.waterfunservice.infrastructure.mapper.RoleMapper;
import org.waterwood.waterfunservice.infrastructure.persistence.utils.RoleSpec;
import org.waterwood.waterfunservice.service.role.RoleService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
public class RoleController {
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @GetMapping("/list")
    public ApiResponse<Page<RoleResp>> listRoles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer parentId
    ){
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Specification<Role> spec = RoleSpec.of(name, parentId);
        Page<RoleResp> roles = roleService.listRoles(spec, pageable)
                .map(roleMapper::toRoleResp);
        return ApiResponse.success(roles);
    }

    @GetMapping("/{id}")
    public ApiResponse<RoleResp> getRole(@PathVariable int id){
        Role role = roleService.getRole(id);
        return ApiResponse.success(roleMapper.toRoleResp(role));
    }

    @PostMapping
    public ApiResponse<Void> addRole(@RequestBody CreateRoleRequest body){
        roleService.addRole(roleMapper.toEntity(body));
        return ApiResponse.success();
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> updateRole(@PathVariable int id, @RequestBody UpdateRoleRequest body){
        Role role = roleMapper.fullUpdate(body, roleService.getRole(id));
        roleService.updateRole(role);
        return ApiResponse.success();
    }

    @PatchMapping("/{id}")
    public ApiResponse<Void> partialUpdateRole(@PathVariable int id, @RequestBody PatchRoleRequest body){
        Role role = roleMapper.partialUpdate(body, roleService.getRole(id));
        roleService.updateRole(role);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable int id){
        roleService.deleteRole(id);
        return ApiResponse.success();
    }
}
