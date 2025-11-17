package org.waterwood.waterfunservice.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.waterwood.waterfunservice.dto.common.enums.PostStatus;
import org.waterwood.waterfunservice.dto.common.enums.PostVisibility;
import org.waterwood.waterfunservice.entity.post.Post;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link Post}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse implements Serializable {
    private String title;
    private String subtitle;
    private String content;
    private String summary;
    private String coverImg;
    private PostStatus status;
    private PostVisibility visibility;
    private Long viewCount;
    private Long likeCount;
    private Long commentCount;
    private Long collectCount;
    private String slug;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
}