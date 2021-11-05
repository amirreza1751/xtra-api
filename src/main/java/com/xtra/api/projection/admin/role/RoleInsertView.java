package com.xtra.api.projection.admin.role;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.user.UserType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoleInsertView {
    @NotBlank(message = "role name can not be empty")
    private String name;
    private String color;

    @NotNull
    private UserType type;
    private Set<String> permissions;
}
