package org.waterwood.waterfunservice.service.post;

import org.waterwood.waterfunservice.entity.post.Tag;

import java.util.List;
import java.util.Set;

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
    Tag getTag(Integer id);

    /**
     * Update a tag
     * @param tag the tag entity {@link Tag}
     */
    void updateTag(Tag tag);

    /**
     * Delete a tag
     * @param id tag id
     */
    void deleteTag(Integer id);

    /**
     * Get tags by tag ids
     * @param tagIds tag ids
     * @return list of {@link Tag}
     */
    Set<Tag> getTags(Iterable<Integer> tagIds, boolean strict);
}
