package com.xtra.api.repository;


import com.xtra.api.model.line.Connection;
import com.xtra.api.model.line.VodConnection;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.projection.admin.connection.VodConnectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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


    @Query(value = "SELECT new com.xtra.api.projection.admin.connection.VodConnectionResult (conn.server.id, count (conn.id)) FROM VodConnection conn WHERE conn.server.id IN ?1 group by conn.server.id")
    List<VodConnectionResult> getVodConnectionsCount(List<Long> servers);

    long countDistinctByLine_OwnerIs(Reseller Owner);
}
