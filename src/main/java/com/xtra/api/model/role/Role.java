package com.xtra.api.model.role;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.xtra.api.model.line.Package;
import com.xtra.api.model.user.UserType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"name", "type"})
)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;

    @Enumerated(EnumType.STRING)
    private UserType type;

    @ToString.Exclude
    @OneToMany(mappedBy = "role", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<PermissionRole> permissions;

    @ManyToMany(mappedBy = "allowedRoles")
    private Set<Package> allowedPackages;

    public Role() {
    }

    public Role(Long id){
        setId(id);
    }

    public void addPermission(PermissionRole permissionRole) {
        if (permissions == null) permissions = new HashSet<>();
        permissions.add(permissionRole);
    }
}
