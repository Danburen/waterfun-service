package org.waterwood.waterfunservice.infrastructure.persistence.utils;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.waterwood.waterfunservice.dto.common.enums.PermissionType;
import org.waterwood.waterfunservice.entity.Permission;

import java.util.ArrayList;
import java.util.List;

public final class PermSpec {
    public static Specification<Permission> of(String name, PermissionType type, String resource, Integer parentId){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> preds = new ArrayList<>();
            if(name != null){
                preds.add(criteriaBuilder.equal(root.get("name"), name));
            }

            if (type != null) {
                preds.add(criteriaBuilder.equal(root.get("type"), type));
            }

            if (resource != null) {
                preds.add(criteriaBuilder.equal(root.get("resource"), resource));
            }

            if(parentId != null){
                preds.add(criteriaBuilder.equal(root.get("parent").get("id"), parentId));
            }
            return criteriaBuilder.and(preds.toArray(new Predicate[0]));
        };
    }
}
