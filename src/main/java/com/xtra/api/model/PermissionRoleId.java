package com.xtra.api.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class PermissionRoleId implements Serializable {
    private Long roleId;
    private String permissionId;

    public PermissionRoleId() {
    }

    public PermissionRoleId(Long roleId, String permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}
