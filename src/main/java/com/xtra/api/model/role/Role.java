package com.xtra.api.model.role;

import com.xtra.api.model.line.Package;
import com.xtra.api.model.user.UserType;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"name", "type"})
)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @NotNull
    private String name;

    @NotNull
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

    public Role(Long id) {
        setId(id);
    }

    public void addPermission(PermissionRole permissionRole) {
        if (permissions == null) permissions = new HashSet<>();
        permissions.add(permissionRole);
    }
}
