package com.xtra.api.projection.admin.role;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.UserType;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoleInsertView {
    private String name;
    private String color;

    private UserType type;

    private Set<String> permissions;
}
