package com.xtra.api.repository;

import com.xtra.api.model.user.User;
import com.xtra.api.model.user.UserType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByUserType(UserType type);
}
