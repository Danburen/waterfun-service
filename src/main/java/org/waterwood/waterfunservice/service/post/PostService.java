package org.waterwood.waterfunservice.service.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.waterwood.waterfunservice.entity.post.Post;

import java.util.Set;

public interface PostService {
    /**
     * Add a post
     * @param entity post entity of {@link  Post} to add
     */
    void add(Post entity, Set<Long> tagIds);

    /**
     * List posts of target author for user.
     * @param spec query specification
     * @param pageable pageable
     * @return  posts
     */
    Page<Post> listPosts(Specification<Post> spec, Pageable pageable);
}
