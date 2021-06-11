package com.xtra.api.repository;


import com.xtra.api.model.stream.StreamServer;
import com.xtra.api.model.stream.StreamServerId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StreamServerRepository extends JpaRepository<StreamServer, StreamServerId> {
    Optional<StreamServer> findById(StreamServerId streamServerId);

    @Override
    void delete(StreamServer entity);

    List<StreamServer> findAllByServerId(long serverId);
}
