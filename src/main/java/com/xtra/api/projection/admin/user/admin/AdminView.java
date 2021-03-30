package com.xtra.api.projection.admin.user.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.projection.admin.user.UserView;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AdminView extends UserView {
    protected String firstname;
    protected String lastname;
}
