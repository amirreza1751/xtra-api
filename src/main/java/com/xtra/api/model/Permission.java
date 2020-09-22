package com.xtra.api.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Permission {
    @Id
    private String pKey;
    private String name;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "permission")
    private List<PermissionRole> permissionAssignments;
}
