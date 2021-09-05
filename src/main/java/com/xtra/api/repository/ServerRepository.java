package com.xtra.api.repository;

import com.xtra.api.model.server.Server;
import com.xtra.api.projection.admin.analytics.ServerSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServerRepository extends JpaRepository<Server, Long> {
    Optional<Server> findByName(String name);

    Optional<Server> findByToken(String token);

    List<Server> findByIdIn(List<Long> ids);

    List<Server> findAllByNameContains(String name);

    @Query(nativeQuery = true,
    value = "SELECT server.name, server.domain_name as domainName , server.ip as ip, server.core_port as corePort, server.nginx_port as nginxPort, server.interface_name as interfaceName\n" +
            "     , count(distinct connection_table.line_id) as onlineUsersCount, count(connection_table.line_id) as connectionsCount, channels_status.offlineChannelsCount, channels_status.onlineChannelsCount\n" +
            "     , resource2.cpu_max_freq as cpuMaxFreq, resource2.memory_total as memoryTotal, resource2.memory_available as memoryAvailable, resource2.network_name as networkName, resource2.network_bytes_recv as networkBytesRecv, resource2.network_bytes_sent as networkBytesSent, resource2.up_time as upTime, resource2.cpuCurrentFreq\n" +
            "from server\n" +
            "         left join (\n" +
            "    SELECT resource.*, avg(rccf.cpu_current_freq) as cpuCurrentFreq from resource\n" +
            "        left join resource_cpu_current_freq rccf on resource.id = rccf.resource_id\n" +
            "    group by resource.id\n" +
            ") as resource2 on server.resource_id = resource2.id\n" +
            "         left join (\n" +
            "    SELECT * from vod_connection\n" +
            "    union\n" +
            "    SELECT * from connection\n" +
            ") as connection_table on connection_table.server_id = server.id\n" +
            "         left join (\n" +
            "    SELECT stream_server.server_id , sd.stream_status, sum(IF(sd.stream_status = 0, 1, 0)) as onlineChannelsCount, sum(IF(sd.stream_status = 1, 1, 0)) as offlineChannelsCount FROM stream_server\n" +
            "    left join stream_details sd on stream_server.stream_details_id = sd.id\n" +
            "    group by stream_server.server_id\n" +
            ") as channels_status on channels_status.server_id = server.id\n" +
            "group by server.name;")
    List<ServerSummary>getServerSummaryList();
}
