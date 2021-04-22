package com.xtra.api.repository;


import com.xtra.api.model.Connection;
import com.xtra.api.model.ConnectionId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, ConnectionId> {
    Optional<Connection> findById(ConnectionId id);

    void deleteById(ConnectionId id);

    int countAllByIdServerId(Long serverId);

    List<Connection> findAllByIdLineId(Long lineId);

    @Override
    Page<Connection> findAll(Pageable pageable);

    int countAllByIdServerIdAndIdStreamId(Long serverId, Long streamId);
}
