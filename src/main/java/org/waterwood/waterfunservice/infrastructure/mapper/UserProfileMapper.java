package org.waterwood.waterfunservice.infrastructure.mapper;

import org.mapstruct.*;
import org.waterwood.waterfunservice.dto.request.user.UpdateUserProfileRequest;
import org.waterwood.waterfunservice.entity.user.UserProfile;
import org.waterwood.waterfunservice.dto.response.user.UserProfileResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileMapper {
    UserProfile toEntity(UserProfileResponse userProfileResponse);

    UserProfileResponse toResponse(UserProfile userProfile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserProfile partialUpdate(UserProfileResponse userProfileResponse, @MappingTarget UserProfile userProfile);

    UserProfile toEntity(UpdateUserProfileRequest body);
}