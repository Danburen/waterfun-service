package org.waterwood.waterfunservice.infrastructure.mapper;

import org.mapstruct.*;
import org.waterwood.waterfunservice.dto.request.user.UpdateUserProfileRequest;
import org.waterwood.waterfunservice.entity.user.User;
import org.waterwood.waterfunservice.dto.response.user.UserInfoResponse;
import org.waterwood.waterfunservice.entity.user.UserProfile;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    UserProfile updateRequestToEntity(UpdateUserProfileRequest request);

    UserInfoResponse toUserInfoResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(UserInfoResponse userInfoResponse, @MappingTarget User user);
}
