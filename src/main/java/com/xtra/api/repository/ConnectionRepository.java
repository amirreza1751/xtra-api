package com.xtra.api.repository;


import com.xtra.api.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    Optional<Connection> findByLineIdAndServerIdAndStreamIdAndUserIp(Long lineId, Long serverId, Long streamId, String userIp);

    void deleteById(Long id);

    int countAllByServerId(Long serverId);

    List<Connection> findAllByLineId(Long lineId);

    @Override
    Page<Connection> findAll(Pageable pageable);

    int countAllByServerIdAndStreamId(Long serverId, Long streamId);

    long countAllByStreamId(Long streamId);
}
