package com.xtra.api.repository;

import com.xtra.api.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    boolean existsAllBypKeyIn(Set<String> keys);


}
