package com.xtra.api.mapper.admin;

import com.xtra.api.model.*;
import com.xtra.api.projection.admin.PermissionView;
import com.xtra.api.projection.admin.role.RoleInsertView;
import com.xtra.api.projection.admin.role.RoleListItem;
import com.xtra.api.projection.admin.role.RoleView;
import com.xtra.api.service.admin.PermissionService;
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

    public abstract RoleView convertToDto(Role role);

    public Set<PermissionView> convertPermissions(Set<PermissionRole> permissions) {
        return permissions.stream().map(permission -> new PermissionView(permission.getPermission().getId().getName(), permission.getPermission().getDescription())).collect(Collectors.toSet());
    }

    @Mapping(target = "permissions", ignore = true)
    public abstract Role convertToEntity(RoleInsertView roleView);

    @AfterMapping
    public void convertPermissionNames(final RoleInsertView roleView, @MappingTarget final Role role) {
        var permissions = new HashSet<PermissionRole>();
        for (var permission : roleView.getPermissions()) {
            var p = permissionService.findByIdOrFail(new PermissionId(permission, roleView.getType()));
            var pr = new PermissionRole(new PermissionRoleId(p.getId(), role.getId()));
            pr.setRole(role);
            pr.setPermission(p);
            permissions.add(pr);
        }
        role.setPermissions(permissions);
    }

    public abstract RoleListItem toListItem(Role role);
}
