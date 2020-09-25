package com.xtra.api.repository;

import com.xtra.api.model.PermissionRole;
import com.xtra.api.model.PermissionRoleId;
import com.xtra.api.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRoleRepository extends JpaRepository<PermissionRole, PermissionRoleId> {
    List<PermissionRole> findAllByRole(Role role);
}
