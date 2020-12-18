package com.xtra.api.mapper;

import com.xtra.api.model.User;
import com.xtra.api.projection.user.UserView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public abstract class UserMapper {

    public abstract UserView convertToView(User user);

}
