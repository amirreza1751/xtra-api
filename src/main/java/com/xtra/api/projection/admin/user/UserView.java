package com.xtra.api.projection.admin.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserView {
    protected Long id;
    protected String username;
    protected String email;
    protected boolean isBanned;
    protected Long roleId;
}
