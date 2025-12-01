package org.waterwood.waterfunservice.infrastructure.mapper;

import org.mapstruct.*;
import org.waterwood.waterfunservice.dto.request.user.UpdateUserProfileRequest;
import org.waterwood.waterfunservicecore.entity.user.UserProfile;
import org.waterwood.waterfunservice.dto.response.user.UserProfileResponse;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserProfileMapper {
    UserProfile toEntity(UserProfileResponse userProfileResponse);

    UserProfileResponse toResponse(UserProfile userProfile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserProfile partialUpdate(UserProfileResponse userProfileResponse, @MappingTarget UserProfile userProfile);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    UserProfile toEntity(UpdateUserProfileRequest body,  @MappingTarget UserProfile entity);
}