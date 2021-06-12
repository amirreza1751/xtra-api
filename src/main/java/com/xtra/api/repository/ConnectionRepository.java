package com.xtra.api.repository;


import com.xtra.api.model.line.Connection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    Optional<Connection> findByLineIdAndServerIdAndStreamIdAndUserIp(Long lineId, Long serverId, Long streamId, String userIp);

    Optional<Connection> findByLineLineTokenAndServerTokenAndStream_StreamTokenAndUserIp(String lineToken, String serverToken, String streamToken, String userIp);

    void deleteById(Long id);

    int countAllByServerId(Long serverId);

    List<Connection> findAllByLineId(Long lineId);

    int countAllByServerIdAndStreamId(Long serverId, Long streamId);

    long countAllByStreamId(Long streamId);

    long countAllByLineId(Long lineId);

    void deleteAllByLastReadIsLessThanEqual(LocalDateTime lastRead);

}
