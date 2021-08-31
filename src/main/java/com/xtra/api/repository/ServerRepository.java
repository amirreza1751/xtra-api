package com.xtra.api.repository;

import com.xtra.api.model.server.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Optional<Server> findByName(String name);

    Optional<Server> findByToken(String token);

    List<Server> findByIdIn(List<Long> ids);

    List<Server> findAllByNameContains(String name);
}
