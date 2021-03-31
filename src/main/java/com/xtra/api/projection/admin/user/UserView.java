package com.xtra.api.projection.admin.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.UserType;
import com.xtra.api.projection.admin.role.RoleView;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserView {
    protected Long id;
    protected String username;
    protected String email;
    protected boolean isBanned;
    protected Long roleId;
}
