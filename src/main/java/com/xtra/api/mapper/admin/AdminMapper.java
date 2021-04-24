package com.xtra.api.mapper.admin;

import com.xtra.api.model.Admin;
import com.xtra.api.model.Role;
import com.xtra.api.projection.admin.user.admin.AdminInsertView;
import com.xtra.api.projection.admin.user.UserSimpleView;
import com.xtra.api.projection.admin.user.admin.AdminView;
import com.xtra.api.service.admin.RoleService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public abstract class AdminMapper {

    @Autowired
    private RoleService roleService;

    public abstract UserSimpleView convertToSimpleView(Admin admin);

    public abstract Admin convertToEntity(AdminInsertView view);

    Role convertToId(Long roleId) {
        if (roleId == null)
            return null;//todo throw exception
        return roleService.findByIdOrFail(roleId);
    }

    @AfterMapping
    void addRole(final AdminInsertView view, @MappingTarget final Admin admin) {
        if (view.getRole() == null)
            return;//todo throw exception
        var role = roleService.findByIdOrFail(view.getRole());
        admin.setRole(role);
    }

    @Mapping(source = "admin.role.id", target = "roleId")
    public abstract AdminView convertToView(Admin admin);

    Long convertRoleToId(Role role) {
        if (role == null)
            return null;
        return role.getId();
    }

}
