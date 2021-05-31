package com.xtra.api.projection.admin.user;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Validated
public class UserInsertView {

    @NotBlank(message = "username can not be empty")
    protected String username;
    @NotBlank(message = "user password is mandatory")
    protected String password;
    @Email
    protected String email;
    protected String _2FASec;
    protected boolean isBanned;

    @NotNull(message = "user role should be determined")
    protected Long roleId;
}
