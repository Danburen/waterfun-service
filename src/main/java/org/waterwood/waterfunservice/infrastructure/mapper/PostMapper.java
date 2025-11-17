package org.waterwood.waterfunservice.infrastructure.mapper;

import org.mapstruct.*;
import org.waterwood.waterfunservice.entity.post.Post;
import org.waterwood.waterfunservice.dto.response.post.PostResponse;
import org.waterwood.waterfunservice.dto.request.post.CreatePostRequest;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {
    Post toEntity(CreatePostRequest createPostRequest);

    CreatePostRequest toCreatePostDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(CreatePostRequest createPostRequest, @MappingTarget Post post);

    Post toEntity(PostResponse postResponse);

    PostResponse toPostResponseDto(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Post partialUpdate(PostResponse postResponse, @MappingTarget Post post);
}