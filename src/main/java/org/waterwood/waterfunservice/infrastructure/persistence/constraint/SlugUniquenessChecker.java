package org.waterwood.waterfunservice.infrastructure.persistence.constraint;

public interface SlugUniquenessChecker {
    boolean existsTagBySlug(String slug);
}
