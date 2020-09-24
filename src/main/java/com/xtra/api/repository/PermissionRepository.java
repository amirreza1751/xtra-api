package com.xtra.api.repository;

import com.xtra.api.model.Permission;
import com.xtra.api.model.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    boolean existsAllBypKeyIn(Set<String> keys);

    List<Permission> findAllByUserType(UserType userType);
}
