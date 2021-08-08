package com.xtra.api.service.admin;

import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.repository.ConnectionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class LoadBalancingService {
    private final ConnectionRepository connectionRepository;

    public LoadBalancingService(ConnectionRepository connectionRepository) {
        this.connectionRepository = connectionRepository;
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
}
