package com.xtra.api.repository;


import com.xtra.api.model.line.Connection;
import com.xtra.api.model.line.VodConnection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VodConnectionRepository extends JpaRepository<VodConnection, Long> {
    Optional<VodConnection> findByLineIdAndServerIdAndVideoIdAndUserIp(Long lineId, Long serverId, Long videoId, String userIp);

    Optional<VodConnection> findByLineLineTokenAndServerTokenAndVideo_tokenAndUserIp(String lineToken, String serverToken, String videoToken, String userIp);

    void deleteById(Long id);

    int countAllByServerId(Long serverId);

    List<VodConnection> findAllByLineId(Long lineId);

    int countAllByServerIdAndVideoId(Long serverId, Long videoId);

    long countAllByVideoId(Long videoId);

    long countAllByLineId(Long lineId);

    List<VodConnection> findAllByLastReadLessThanEqual(LocalDateTime lastRead);

    void deleteAllByLastReadIsLessThanEqual(LocalDateTime lastRead);

}
