package com.xtra.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.PermissionRole;
import com.xtra.api.model.UserType;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RoleDTO {
    private long id;
    private String name;
    private String color;

    private UserType userType;

    private Map<String,String> permissions;
}
