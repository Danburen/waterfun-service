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
    void add(Post entity, Set<Integer> tagIds);

    /**
     * List posts of target author for user.
     * @param spec query specification
     * @param pageable pageable
     * @return  posts
     */
    Page<Post> listPosts(Specification<Post> spec, Pageable pageable);

    /**
     * Delete current user's post
     * @param id post id
     */
    void deletePost(Long id);

    /**
     * Get post entity by id
     * @param id post id
     * @return post entity
     */
    Post getPostById(Long id);

    /**
     * Update post
     *
     * @param p          post entity
     * @param tagIds     tag ids
     * @param categoryId category id
     */
    void updatePost(Post p, Set<Integer> tagIds, Integer categoryId);
}
