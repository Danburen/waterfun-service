package org.waterwood.waterfunservice.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.waterwood.waterfunservice.entity.post.Tag;
import org.waterwood.waterfunservice.infrastructure.persistence.constraint.SlugUniquenessChecker;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long>, SlugUniquenessChecker {
  boolean existsTagBySlug(String slug);

  List<Tag> findAllByCreatorId(Long currentUserId);
}