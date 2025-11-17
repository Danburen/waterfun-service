package org.waterwood.waterfunservice.service.role;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.waterwood.waterfunservice.entity.Permission;
import org.waterwood.waterfunservice.entity.Role;

import java.util.List;

public interface RoleService {
    /**
     * Get permissions by role ID
     * @param roleId role ID
     * @return  permissions
     */
    @Transactional(readOnly = true)
    List<Permission> getPermissions(int roleId);

    /**
     * List all the roles
     * @param spec  specification
     * @param pageable pageable
     * @return Page of role
     */
    Page<Role> listRoles(Specification<Role> spec, Pageable pageable);

    /**
     * Get role by ID
     * @param id role ID
     * @return role
     */
    Role getRole(int id);

    /**
     * Add a role, the role must not exist
     * @param entity role
     */
    Role addRole(Role entity);

    /**
     * Update role, the role ID must be set
     * @param role  role
     */
    Role updateRole(Role role);

    /**
     * Delete role
     * @param id role ID
     */
    void deleteRole(int id);
}
