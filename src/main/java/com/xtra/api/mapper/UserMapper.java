package com.xtra.api.mapper;

import com.xtra.api.model.User;
import com.xtra.api.projection.UserInsertView;
import com.xtra.api.projection.UserView;
import com.xtra.api.service.RoleService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public abstract class UserMapper {

    @Autowired
    private RoleService roleService;

    public abstract User convertToEntity(UserInsertView view);

    @AfterMapping
    void addRole(final UserInsertView view, @MappingTarget final User user) {
        if (view.getRoleId() == null)
            return;//todo throw exception
        var role = roleService.findByIdOrFail(view.getRoleId());
        user.setRole(role);
    }

    public abstract UserView convertToDto(User user);
}
