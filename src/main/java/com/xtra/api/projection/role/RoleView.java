package com.xtra.api.projection.role;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.UserType;
import com.xtra.api.projection.PermissionView;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoleView {
    private long id;
    private String name;
    private String color;

    private UserType type;

    private Set<PermissionView> permissions;
}
