package com.xtra.api.repository;

import com.xtra.api.model.server.Resource;
import com.xtra.api.projection.admin.analytics.NetworkBytesSum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;


public interface ResourceRepository extends JpaRepository<Resource, Long> {
    Optional<Resource> findByServerId(Long serverId);

    @Query(nativeQuery = true, value = "SELECT sum(network_bytes_sent) as networkBytesSent , sum(network_bytes_recv) as networkBytesRecv FROM resource;")
    NetworkBytesSum networksBytesSum();
}
