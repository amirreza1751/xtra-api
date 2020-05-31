package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
public class PermissionRole {
    @EmbeddedId
    private PermissionRoleId id = new PermissionRoleId() ;

    @ManyToOne
    @MapsId("permissionId")
    Permission permission;

    @ManyToOne
    @MapsId("roleId")
    Role role;

    private String value;

    @Data
    @Embeddable
    private static class PermissionRoleId implements Serializable{
        private static final long serialVersionUID = 1L;
        private Long permissionId;
        private Long roleId;
    }
}
