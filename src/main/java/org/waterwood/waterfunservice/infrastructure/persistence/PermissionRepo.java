package org.waterwood.waterfunservice.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.waterwood.waterfunservice.entity.Permission;

import java.util.List;
import java.util.Optional;

public interface PermissionRepo extends JpaRepository<Permission, Integer>, JpaSpecificationExecutor<Permission> {
    List<Permission> findByName(String name);
    Optional<Permission> findByCode(String code);
}
