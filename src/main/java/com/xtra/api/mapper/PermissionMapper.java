package com.xtra.api.mapper;

import com.xtra.api.model.Permission;
import com.xtra.api.projection.PermissionView;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(source = "id.name",target = "name")
    PermissionView convertToDto(Permission permission);
}
