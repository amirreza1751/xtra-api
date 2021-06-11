package com.xtra.api.mapper.admin;

import com.xtra.api.model.role.Permission;
import com.xtra.api.projection.admin.PermissionView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(source = "id.name",target = "name")
    PermissionView convertToDto(Permission permission);
}
