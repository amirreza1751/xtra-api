package com.xtra.api.repository;

import com.xtra.api.model.Server;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Optional<Server> findByName(String name);
    boolean existsAllByIdIn (ArrayList<Long> serverIds);
    Optional<Server> findByIpAndCorePort(String ip, String port);
}
