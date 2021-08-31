package com.xtra.api.service.admin;

import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.model.vod.Video;
import com.xtra.api.projection.admin.connection.VodConnectionResult;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.VodConnectionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoadBalancingService {
    private final ConnectionRepository connectionRepository;
    private final VodConnectionRepository vodConnectionRepository;

    public LoadBalancingService(ConnectionRepository connectionRepository, VodConnectionRepository vodConnectionRepository) {
        this.connectionRepository = connectionRepository;
        this.vodConnectionRepository = vodConnectionRepository;
    }

    public ArrayList<Server> findAvailableServers(Stream stream) {
        ArrayList<Server> result = new ArrayList<>();
        stream.getStreamServers().forEach(streamServer -> result.add(streamServer.getServer()));
        return result;
    }

    private final ArrayList<Integer> connections = new ArrayList<>();

    public Server findLeastConnServer(ArrayList<Server> servers) {
//        servers.forEach(server -> connections.add(connectionRepository.countAllByServerId(server.getId())));
        return servers.get(0);
    }

    public ArrayList<Server> findAvailableServersForVod(Video video) {
        ArrayList<Server> result = new ArrayList<>();
        video.getVideoServers().forEach(videoServer -> result.add(videoServer.getServer()));
        return result;
    }

    public Server findLeastConnServerForVod(ArrayList<Server> servers) {
        List<Long> serverIds =  servers.stream().map(Server::getId).collect(Collectors.toList());
        long selectedServer = serverIds.get(0); //Default Server.
        var vodConnectionsCount = vodConnectionRepository.getVodConnectionsCount(serverIds);
        if (vodConnectionsCount.size() != 0) {
            selectedServer = Collections.min(vodConnectionsCount).getServerId();
            for (Long temp : serverIds) {
                var tempResult = new VodConnectionResult(temp);
                if (!vodConnectionsCount.contains(tempResult)) {
                    selectedServer = tempResult.getServerId();
                    break;
                }
            }
        }
        long finalSelectedServer = selectedServer;
        return servers.stream().filter(server -> server.getId().equals(finalSelectedServer)).findFirst().orElseThrow(() -> new EntityNotFoundException("Server", finalSelectedServer));
    }
}
