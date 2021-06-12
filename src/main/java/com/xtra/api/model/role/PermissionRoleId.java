package com.xtra.api.model.role;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class PermissionRoleId implements Serializable {
    private PermissionId permissionId;

    private Long roleId;

    public PermissionRoleId() {
    }

    public PermissionRoleId(PermissionId permissionId, Long roleId) {
        this.permissionId = permissionId;
        this.roleId = roleId;
    }
}
