package org.waterwood.waterfunservice.service.role;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.entity.Permission;
import org.waterwood.waterfunservice.entity.Role;
import org.waterwood.waterfunservice.entity.RolePermission;
import org.waterwood.waterfunservice.infrastructure.exception.business.BusinessException;
import org.waterwood.waterfunservice.infrastructure.persistence.RolePermRepo;
import org.waterwood.waterfunservice.infrastructure.persistence.RoleRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepo roleRepo;
    private final RolePermRepo rolePermRepo;


    @Transactional(readOnly = true)
    @Override
    public List<Permission> getPermissions(int roleId){
        return roleRepo.findById(roleId).map(role ->
                        rolePermRepo.findByRole(role).stream()
                        .map(RolePermission::getPermission)
                        .toList())
                .orElse(List.of());
    }

    @Override
    public Page<Role> listRoles(Specification<Role> spec, Pageable pageable) {
        // TODO: check permission
        return roleRepo.findAll(spec, pageable);
    }

    @Override
    public Role getRole(int id) {
        // TODO: check permission
        return roleRepo.findById(id)
                .orElseThrow(()-> new BusinessException(ResponseCode.ROLE_NOT_FOUND));
    }

    @Override
    public Role addRole(Role role) {
        // TODO: check permission
        if(roleRepo.existsById(role.getId())){
            throw new BusinessException(ResponseCode.ROLE_ALREADY_EXISTS);
        }
        return roleRepo.save(role);
    }

    @Override
    public Role updateRole(Role role) {
        // TODO: check permission
        if(role.getParent() != null && role.getParent().getId() != null){
            role.setParent(roleRepo.getReferenceById(role.getParent().getId()));
        }
        return roleRepo.save(role);
    }

    @Override
    public void deleteRole(int id) {
        // TODO: check permission
        roleRepo.deleteById(id);
    }
}