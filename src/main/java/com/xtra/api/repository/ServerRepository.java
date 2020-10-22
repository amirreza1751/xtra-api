package com.xtra.api.repository;

import com.xtra.api.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Optional<Server> findByName(String name);
    boolean existsAllByIdIn (ArrayList<Long> serverIds);
    Optional<Server> findByIp(String ip);
}
