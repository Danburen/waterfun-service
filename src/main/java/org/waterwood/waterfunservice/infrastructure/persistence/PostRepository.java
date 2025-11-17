package org.waterwood.waterfunservice.infrastructure.persistence;

import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.waterwood.waterfunservice.entity.post.Post;
import org.waterwood.waterfunservice.infrastructure.persistence.constraint.SlugUniquenessChecker;

public interface PostRepository extends JpaRepository<Post, Long>,
        JpaSpecificationExecutor<Post>,
        SlugUniquenessChecker {
    boolean existsBySlug(String slug);

}
