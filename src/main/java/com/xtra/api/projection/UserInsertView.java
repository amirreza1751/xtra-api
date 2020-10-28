package com.xtra.api.projection;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.UserType;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInsertView {
    protected Long id;

    protected String username;
    protected String password;

    protected String email;
    protected String _2FASec;
    protected boolean isBanned;
    protected UserType userType;

    protected Long roleId;
}
