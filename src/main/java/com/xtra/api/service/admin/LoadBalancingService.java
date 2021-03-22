package com.xtra.api.service.admin;

import com.xtra.api.model.Server;
import com.xtra.api.model.Stream;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.StreamRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class LoadBalancingService {
    private final StreamRepository streamRepository;
    private final LineActivityRepository lineActivityRepository;

    public LoadBalancingService(StreamRepository streamRepository, LineActivityRepository lineActivityRepository) {
        this.streamRepository = streamRepository;
        this.lineActivityRepository = lineActivityRepository;
    }

    public ArrayList<Server> findAvailableServers(String stream_token) {
        Stream stream = (Stream) streamRepository.getByStreamToken(stream_token).get();
        ArrayList<Server> result = new ArrayList<>();
        stream.getStreamServers().forEach(streamServer -> result.add(streamServer.getServer()));
        return result;
    }

    private final ArrayList<Integer> connections = new ArrayList<>();

    public Server findLeastConnServer(ArrayList<Server> servers) {
        servers.forEach(server -> connections.add(lineActivityRepository.countAllByIdServerId(server.getId())));
         return servers.get(connections.indexOf(Collections.min(connections)));
    }
}
