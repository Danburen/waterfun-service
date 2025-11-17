package org.waterwood.waterfunservice.infrastructure.mapper;

import org.mapstruct.*;
import org.waterwood.waterfunservice.dto.request.post.UpdateTagRequest;
import org.waterwood.waterfunservice.entity.post.Tag;
import org.waterwood.waterfunservice.dto.request.post.CreateTagRequest;
import org.waterwood.waterfunservice.dto.response.post.TagResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface TagMapper {
    Tag toEntity(TagResponse tagResponse);

    TagResponse toResponseDto(Tag tag);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Tag partialUpdate(TagResponse tagResponse, @MappingTarget Tag tag);

    Tag toEntity(CreateTagRequest request);

    Tag toEntity(UpdateTagRequest request);
}