package com.xtra.api.model.role;

import com.xtra.api.model.user.UserType;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
@Data
public class PermissionId implements Serializable {
    private String name;
    @Enumerated(EnumType.STRING)
    private UserType userType;

    public PermissionId(String name, UserType userType) {
        this.name = name;
        this.userType = userType;
    }

    public PermissionId() {
    }
}
