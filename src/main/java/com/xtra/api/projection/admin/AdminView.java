package com.xtra.api.projection.admin;

import com.xtra.api.model.UserType;
import com.xtra.api.projection.role.RoleView;
import lombok.Data;

@Data
public class AdminView {
    protected long id;

    protected String firstname;
    protected String lastname;
    protected String username;

    protected String email;
    protected String _2FASec;
    protected boolean isBanned;
    protected UserType userType;

    protected Long role;
}