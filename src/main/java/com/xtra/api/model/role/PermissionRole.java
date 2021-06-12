package com.xtra.api.model.role;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

@Entity
@Data
public class PermissionRole {
    @EmbeddedId
    private PermissionRoleId id;

    @ManyToOne
    @MapsId("permissionId")
    @EqualsAndHashCode.Exclude
    private Permission permission;

    @ManyToOne
    @MapsId("roleId")
    @EqualsAndHashCode.Exclude
    private Role role;


    public PermissionRole(PermissionRoleId id) {
        this.id = id;
    }

    public PermissionRole() {
    }
}
