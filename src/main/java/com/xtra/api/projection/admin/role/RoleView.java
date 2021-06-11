package com.xtra.api.projection.admin.role;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.user.UserType;
import com.xtra.api.projection.admin.PermissionView;
import lombok.Data;

import java.util.Set;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoleView {
    private Long id;
    private String name;
    private String color;

    private UserType type;

    private Set<PermissionView> permissions;
}
