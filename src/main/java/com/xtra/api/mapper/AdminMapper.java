package com.xtra.api.mapper;

import com.xtra.api.model.Admin;
import com.xtra.api.model.Role;
import com.xtra.api.projection.admin.AdminInsertView;
import com.xtra.api.projection.admin.AdminSimpleView;
import com.xtra.api.projection.admin.AdminView;
import com.xtra.api.service.RoleService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public abstract class AdminMapper {

    @Autowired
    private RoleService roleService;

    public abstract AdminSimpleView convertToSimpleView(Admin admin);

    public abstract Admin convertToEntity(AdminInsertView view);

    @AfterMapping
    void addRole(final AdminInsertView view, @MappingTarget final Admin admin) {
        if (view.getRoleId() == null)
            return;//todo throw exception
        var role = roleService.findByIdOrFail(view.getRoleId());
        admin.setRole(role);
    }

    public abstract AdminView convertToView(Admin admin);

    Long convertRoleToId(Role role) {
        return role.getId();
    }

}