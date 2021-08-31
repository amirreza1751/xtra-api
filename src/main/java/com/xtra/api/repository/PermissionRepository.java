package com.xtra.api.repository;

import com.xtra.api.model.role.Permission;
import com.xtra.api.model.role.PermissionId;
import com.xtra.api.model.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, PermissionId> {
    List<Permission> findAllByIdUserType(UserType userType);
}
