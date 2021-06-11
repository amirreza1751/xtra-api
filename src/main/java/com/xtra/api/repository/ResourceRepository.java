package com.xtra.api.repository;

import com.xtra.api.model.server.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Optional<Resource> findByServerId(Long serverId);
}
