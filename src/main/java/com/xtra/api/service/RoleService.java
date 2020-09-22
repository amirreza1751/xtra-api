package com.xtra.api.service;

import com.xtra.api.dto.RoleDTO;
import com.xtra.api.model.PermissionRole;
import com.xtra.api.model.PermissionRoleId;
import com.xtra.api.model.Role;
import com.xtra.api.repository.RoleRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RoleService extends CrudService<Role, Long, RoleRepository> {
    private final ModelMapper modelMapper;
    private final PermissionService permissionService;
    private final PermissionRoleService permissionRoleService;

    @Autowired
    protected RoleService(RoleRepository repository, PermissionService permissionService, PermissionRoleService permissionRoleService) {
        super(repository, Role.class);
        this.permissionService = permissionService;
        this.permissionRoleService = permissionRoleService;
        modelMapper = new ModelMapper();
    }

    @Override
    protected Page<Role> findWithSearch(Pageable page, String search) {
        return null;
    }

//    protected RoleDTO convertToDto(Role role) {
//        RoleDTO roleDTO = modelMapper.map(role, RoleDTO.class);
//
//    }

    protected Role convertToEntity(RoleDTO roleDTO) {
        Role role = modelMapper.map(roleDTO, Role.class);

        if (role.getId() == null) {
            role.setId(add(role).getId());
        }
        Long roleId = role.getId();
        Set<String> keys = roleDTO.getPermissions().keySet();
        if (!permissionService.existsAllByKeys(keys)) {
            throw new RuntimeException("at least of one the keys are wrong");
        }
        ArrayList<PermissionRole> permissionAssignments = new ArrayList<>();

        for (String key : keys) {
            PermissionRole permissionAssign = new PermissionRole();
            permissionAssign.setId(new PermissionRoleId(roleId, key));
            permissionAssign.setValue(roleDTO.getPermissions().get(key));
            permissionRoleService.add(permissionAssign);
            permissionAssignments.add(permissionAssign);
        }
        role.setPermissionAssignments(permissionAssignments);
        return role;
    }
}
