package com.xtra.api.service.admin;

import com.xtra.api.model.Server;
import com.xtra.api.model.Stream;
import com.xtra.api.repository.ConnectionRepository;
import com.xtra.api.repository.StreamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class LoadBalancingService {
    private final StreamRepository streamRepository;
    private final ConnectionRepository connectionRepository;

    public LoadBalancingService(StreamRepository streamRepository, ConnectionRepository connectionRepository) {
        this.streamRepository = streamRepository;
        this.connectionRepository = connectionRepository;
    }

    public ArrayList<Server> findAvailableServers(String stream_token) {
        Stream stream = (Stream) streamRepository.findByStreamToken(stream_token).get();
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
