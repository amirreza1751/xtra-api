package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class PermissionRole {
    @EmbeddedId
    private PermissionRoleId id;

    @ManyToOne
    @JsonBackReference("permission_id")
    @MapsId("permissionId")
    Permission permission;

    @ManyToOne
    @JsonBackReference("role_id")
    @MapsId("roleId")
    Role role;

    private String value;
}
