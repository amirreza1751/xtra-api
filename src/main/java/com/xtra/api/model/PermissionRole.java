package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

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
