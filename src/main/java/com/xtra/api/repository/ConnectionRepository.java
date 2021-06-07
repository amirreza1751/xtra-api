package com.xtra.api.repository;


import com.xtra.api.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
    Optional<Connection> findByLineIdAndServerIdAndStreamIdAndUserIp(Long lineId, Long serverId, Long streamId, String userIp);

    Optional<Connection> findByLineLineTokenAndServerTokenAndStream_StreamTokenAndUserIp(String lineToken, String serverToken, String streamToken, String userIp);

    void deleteById(Long id);

    int countAllByServerId(Long serverId);

    List<Connection> findAllByLineId(Long lineId);

    Page<Connection> findAllByKilledFalse(Pageable pageable);

    int countAllByServerIdAndStreamId(Long serverId, Long streamId);

    long countAllByStreamId(Long streamId);

    long countAllByLineId(Long lineId);

    void deleteAllByKilledTrueAndEndDateBefore(LocalDateTime endedDate);

    void deleteAllByKilledFalseAndLastReadIsLessThanEqual(LocalDateTime lastRead);

    @Modifying
    @Query("update Connection c set c.killed = true, c.endDate = :dateTime where c.line.id = :lineId")
    void killAllLineConnectionsByLineId(Long lineId, LocalDateTime dateTime);
}
