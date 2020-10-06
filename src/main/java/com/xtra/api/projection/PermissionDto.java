package com.xtra.api.projection;

import com.xtra.api.model.UserType;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class PermissionDto {
    private String pKey;
    private String name;
    private boolean isNumeric;

    @Enumerated(EnumType.STRING)
    private UserType userType;
}
