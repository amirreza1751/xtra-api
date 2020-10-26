package com.xtra.api.repository;


import com.xtra.api.model.StreamServer;
import com.xtra.api.model.StreamServerId;
import org.checkerframework.checker.nullness.Opt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StreamServerRepository extends JpaRepository<StreamServer, StreamServerId> {
    Optional<StreamServer> findById(StreamServerId streamServerId);
}
