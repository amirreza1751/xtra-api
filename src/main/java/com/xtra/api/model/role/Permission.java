package com.xtra.api.model.role;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Permission {
    @EmbeddedId
    private PermissionId id;
    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "permission")
    private Set<PermissionRole> roles;

    @ManyToOne
    private Permission parent;

    @OneToMany(mappedBy = "parent")
    private Set<Permission> permissions;

    public void addRole(PermissionRole role) {
        if (roles == null) roles = new HashSet<>();
        roles.add(role);
    }

    public void removeRole(PermissionRole role) {
        roles.remove(role);
    }
}
