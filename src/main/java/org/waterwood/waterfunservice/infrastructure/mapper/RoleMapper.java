package org.waterwood.waterfunservice.infrastructure.mapper;

import org.mapstruct.*;
import org.waterwood.waterfunservice.dto.request.role.CreateRoleRequest;
import org.waterwood.waterfunservice.dto.request.role.UpdateRoleRequest;
import org.waterwood.waterfunservice.entity.PatchRoleRequest;
import org.waterwood.waterfunservice.entity.Role;
import org.waterwood.waterfunservice.dto.response.role.RoleResp;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface RoleMapper {
    @Mapping(source = "parentId", target = "parent.id")
    Role toEntity(RoleResp roleResp);

    @Mapping(source = "parent.id", target = "parentId")
    RoleResp toRoleResp(Role role);

    @Mapping(source = "parentId", target = "parent.id")
    Role toEntity(CreateRoleRequest createRoleRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    Role fullUpdate(UpdateRoleRequest updateRoleRequest, @MappingTarget Role role);

    @Mapping(source = "parentId", target = "parent.id")
    Role toEntity(PatchRoleRequest patchRoleRequest);

    @Mapping(source = "parent.id", target = "parentId")
    PatchRoleRequest toDto(Role role);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Role partialUpdate(PatchRoleRequest patchRoleRequest, @MappingTarget Role role);
}