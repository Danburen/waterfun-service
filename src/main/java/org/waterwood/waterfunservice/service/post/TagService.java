package org.waterwood.waterfunservice.service.post;

import org.waterwood.waterfunservice.entity.post.Tag;

import java.util.List;

public interface TagService {
    /**
     * Create a new tag
     * @param tag the tag entity {@link Tag}
     */
    void createTag(Tag tag);

    /**
     * Return the list of Tags created by current user's tags
     * @return list of {@link Tag}
     */
    List<Tag> getTags();

    /**
     * Get a tag by id
     * @param id tag id
     * @return {@link Tag}
     */
    Tag getTag(Long id);

    /**
     * Update a tag
     * @param tag the tag entity {@link Tag}
     */
    void updateTag(Tag tag);

    /**
     * Delete a tag
     * @param id tag id
     */
    void deleteTag(Long id);
}
