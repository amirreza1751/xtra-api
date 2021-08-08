package com.xtra.api.service.admin;

import com.xtra.api.mapper.system.StreamMapper;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.model.stream.StreamDetails;
import com.xtra.api.model.stream.StreamServer;
import com.xtra.api.repository.StreamBaseRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.CrudService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class StreamBaseService<S extends Stream, R extends StreamBaseRepository<S>> extends CrudService<S, Long, R> {
    private final ServerService serverService;
    private final StreamServerRepository streamServerRepository;

    protected StreamBaseService(R repository, String entityName, ServerService serverService, StreamMapper streamMapper, StreamServerRepository streamServerRepository) {
        super(repository, entityName);
        this.serverService = serverService;
        this.streamServerRepository = streamServerRepository;
    }

    public S findById(Long id) {
        return repository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public Stream findByTokenOrFail(String token) {
        return repository.findByStreamToken(token).orElseThrow(() -> new EntityNotFoundException("Stream", token));
    }

    public Long findIdByToken(String token) {
        return findByTokenOrFail(token).getId();
    }


    public void stopStreamOnServers(Long id, List<Long> serverIds) {
        var channel = findByIdOrFail(id);
        for (StreamServer streamServer : channel.getStreamServers()) {
            if (serverIds == null || serverIds.contains(streamServer.getServer().getId())) {
                serverService.sendStopRequest(id, streamServer.getServer());
                streamServer.setStreamDetails(new StreamDetails());
                streamServerRepository.save(streamServer);
            }
        }
    }

    protected List<Server> getServersForStream(Stream stream, Set<Long> serverIds) {
        if (serverIds == null || serverIds.size() == 0)
            return stream.getStreamServers().stream().map(StreamServer::getServer).collect(Collectors.toList());
        return stream.getStreamServers().stream().map(StreamServer::getServer)
                .filter(server -> serverIds.contains(server.getId())).collect(Collectors.toList());
    }

}
