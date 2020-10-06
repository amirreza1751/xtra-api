package com.xtra.api.facade;

import com.xtra.api.projection.RoleDto;
import com.xtra.api.model.PermissionRole;
import com.xtra.api.model.PermissionRoleId;
import com.xtra.api.model.Role;
import com.xtra.api.service.PermissionRoleService;
import com.xtra.api.service.PermissionService;
import com.xtra.api.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleFacade {
    private final ModelMapper modelMapper;
    private final PermissionService permissionService;
    private final RoleService roleService;
    private final PermissionRoleService permissionRoleService;

    @Autowired
    public RoleFacade(PermissionService permissionService, RoleService roleService, PermissionRoleService permissionRoleService) {
        this.modelMapper = new ModelMapper();
        this.permissionService = permissionService;
        this.roleService = roleService;
        this.permissionRoleService = permissionRoleService;
    }

    public Role convertToEntity(RoleDto roleDTO) {
        Role role = modelMapper.map(roleDTO, Role.class);

        if (role.getId() == null || role.getId() == 0) {
            role.setId(roleService.add(role).getId());
        }
        Long roleId = role.getId();
        Set<String> keys = roleDTO.getPermissions().stream().map(permission -> permission[0]).collect(Collectors.toSet());
        if (!permissionService.existsAllByKeys(keys)) {
            throw new RuntimeException("at least of one the permission keys are not found");
        }
        ArrayList<PermissionRole> permissionAssignments = new ArrayList<>();

        for (String[] p : roleDTO.getPermissions()) {
            PermissionRole permissionAssign = new PermissionRole();
            permissionAssign.setId(new PermissionRoleId(roleId, p[0]));
            permissionAssign.setValue(p[1]);

            var per = permissionService.findByIdOrFail(p[0]);
            permissionAssign.setPermission(per);
            permissionAssign.setRole(role);
            per.addPermissionAssignment(permissionAssign);
            permissionService.updateOrFail(per.getPKey(), per);

            permissionRoleService.add(permissionAssign);
            permissionAssignments.add(permissionAssign);
        }
        role.setPermissionAssignments(permissionAssignments);
        return role;
    }

    public RoleDto convertToDto(Role role) {
        RoleDto roleDTO = modelMapper.map(role, RoleDto.class);
        Set<String[]> permissions = role.getPermissionAssignments().stream().map(e -> new String[]{e.getId().getPermissionId(), e.getValue()}).collect(Collectors.toSet());
        roleDTO.setPermissions(permissions);
        return roleDTO;
    }
}
