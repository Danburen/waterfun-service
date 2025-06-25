package org.waterwood.waterfunservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.permission.Role;

public interface RoleRepo extends JpaRepository<Role, Long> {
}
