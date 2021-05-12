package com.xtra.api.projection.admin.role;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.UserType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoleInsertView {
    private long id;
    @NotBlank( message = "role name can not be empty" )
    private String name;
    @NotBlank( message = "role color can not be empty" )
    private String color;

    private UserType type;

    @NotNull( message = "role permissions can not be empty" )
    private Set<String> permissions;
}
