package com.xtra.api.service.admin;

import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.model.vod.Video;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.VodConnectionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

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
        servers.forEach(server -> connections.add(connectionRepository.countAllByServerId(server.getId())));
        var vodConnectionsCount = vodConnectionRepository.getVodConnectionsCount();
        var leastConn = Collections.min(vodConnectionsCount);
        return servers.stream().filter(server -> server.getId().equals(leastConn.getServerId())).findFirst().orElseThrow(() -> new EntityNotFoundException("Server", leastConn.getServerId()));
    }
}
