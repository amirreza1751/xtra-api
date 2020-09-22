package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class PermissionRole {
    @EmbeddedId
    private PermissionRoleId id;

    @ManyToOne
    @MapsId("permissionId")
    Permission permission;

    @ManyToOne
    @MapsId("roleId")
    Role role;

    private String value;
}
