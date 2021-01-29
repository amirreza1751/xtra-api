package com.xtra.api.mapper.admin;

import com.xtra.api.model.User;
import com.xtra.api.projection.admin.user.UserView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public abstract class UserMapper {

    public abstract UserView convertToView(User user);

}
