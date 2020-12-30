package com.xtra.api.projection.user;

import lombok.Data;

@Data
public class UserInsertView {
    protected Long id;
    protected String username;
    protected String password;
    protected String email;
    protected String _2FASec;
    protected boolean isBanned;

    protected Long role;
}
