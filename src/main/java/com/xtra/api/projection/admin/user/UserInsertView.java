package com.xtra.api.projection.admin.user;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UserInsertView {
    protected String username;
    protected String password;
    protected String email;
    protected String _2FASec;
    protected boolean isBanned;

    protected Long roleId;
}
