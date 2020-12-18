package com.xtra.api.projection.admin;

import com.xtra.api.model.UserType;
import lombok.Data;

@Data
public class AdminInsertView{
    protected Long id;
    protected String username;
    protected String password;
    private String firstName;
    private String lastName;
    protected String email;
    protected String _2FASec;
    protected boolean isBanned;
    protected UserType userType;

    protected Long roleId;
}
