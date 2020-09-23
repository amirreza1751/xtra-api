package com.xtra.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Permission {
    @Id
    private String pKey;
    private String name;
    private boolean isNumeric;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @JsonManagedReference("permission_id")
    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    private List<PermissionRole> permissionAssignments = new ArrayList<>();

    public void addPermissionAssignment(PermissionRole permissionRole) {
        permissionAssignments.add(permissionRole);
    }
}
