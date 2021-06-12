package com.xtra.api.repository;

import com.xtra.api.model.role.Role;
import com.xtra.api.model.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByTypeAndName(UserType type, String name);

    List<Role> findAllByType(UserType type);
}
