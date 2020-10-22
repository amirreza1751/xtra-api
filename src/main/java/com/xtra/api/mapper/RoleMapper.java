package com.xtra.api.mapper;

import com.xtra.api.model.Permission;
import com.xtra.api.model.PermissionId;
import com.xtra.api.model.Role;
import com.xtra.api.projection.RoleView;
import com.xtra.api.service.PermissionService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = PermissionMapper.class)
public abstract class RoleMapper {
    @Autowired
    private PermissionService permissionService;

    @Mapping(source = "permissions", target = "permissions")
    public abstract RoleView convertToDto(Role role);

    public Set<String> convertPermissions(Set<Permission> permissions) {
        return permissions.stream().map(permission -> permission.getId().getName()).collect(Collectors.toSet());
    }


    public abstract Role convertToEntity(RoleView roleView);

    @AfterMapping
    public void convertPermissionNames(final RoleView roleView, @MappingTarget final Role role) {
        var permissions = new HashSet<Permission>();
        for (var permission : roleView.getPermissions()) {
            permissions.add(permissionService.findByIdOrFail(new PermissionId(permission.getName(), roleView.getType())));
        }
        role.setPermissions(permissions);
    }
}
