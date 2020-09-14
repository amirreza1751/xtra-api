package com.xtra.api.model;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    private String pKey;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @OneToMany(mappedBy = "permission")
    private List<PermissionRole> permissionAssignments;
}
