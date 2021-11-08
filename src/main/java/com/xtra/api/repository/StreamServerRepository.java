package com.xtra.api.repository;


import com.xtra.api.model.stream.StreamServer;
import com.xtra.api.model.stream.StreamServerId;
import com.xtra.api.model.stream.StreamStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StreamServerRepository extends JpaRepository<StreamServer, StreamServerId> {
    Optional<StreamServer> findById(StreamServerId streamServerId);

    @Override
    void delete(StreamServer entity);

    List<StreamServer> findAllByServerId(long serverId);

    List<StreamServer> findAllByStreamDetailsLastUpdatedBefore(LocalDateTime time);
    long countByStreamDetails_StreamStatusIs(StreamStatus streamStatus);
}
