package com.xtra.api.service.admin;

import com.xtra.api.exception.EntityNotFoundException;
import com.xtra.api.mapper.system.StreamMapper;
import com.xtra.api.model.*;
import com.xtra.api.projection.system.StreamDetailsView;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.service.CrudService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

public abstract class StreamService<S extends Stream, R extends StreamRepository<S>> extends CrudService<S, Long, R> {
    private final ServerService serverService;
    private final StreamMapper streamMapper;

    protected StreamService(R repository, String className, ServerService serverService, StreamMapper streamMapper) {
        super(repository, className);
        this.serverService = serverService;
        this.streamMapper = streamMapper;
    }

    public S findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException());
    }

    public Stream findByTokenOrFail(String token) {
        return repository.findByStreamToken(token).orElseThrow(() -> new EntityNotFoundException("Stream", token));
    }

    public Long findIdByToken(String token) {
        return findByTokenOrFail(token).getId();
    }

    public boolean startOrFail(Long id, List<Long> serverIds) {
        for (Server server : getStreamServers(id, serverIds)) {
            serverService.sendStartRequest(id, server);
        }
        return true;
    }

    public boolean stopOrFail(Long id, List<Long> serverIds) {
        Optional<S> ch = repository.findById(id);
        for (Server server : getStreamServers(id, serverIds)) {
            if (ch.isPresent()) {
                S channel = ch.get();
                Set<StreamServer> streamServers = channel.getStreamServers();
                for (StreamServer streamServer : streamServers) {
                    if (streamServer.getServer().getId().equals(server.getId())) {
                        streamServer.setStreamDetails(new StreamDetails());
                    }
                }
                channel.setStreamServers(streamServers);
                repository.save(channel);
            }
            serverService.sendStopRequest(id, server);
        }
        return true;
    }

    public boolean restartOrFail(Long id, List<Long> serverIds) {
        for (Server server : getStreamServers(id, serverIds)) {
            serverService.sendRestartRequest(id, server);
        }
        return true;
    }

    //@todo possible database optimizations necessary
    private List<Server> getStreamServers(Long id, List<Long> serverIds) {
        List<Server> servers;
        var stream = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("id", id));
        if (serverIds == null) {
            servers = stream.getStreamServers().stream().map(StreamServer::getServer).collect(Collectors.toList());
        } else servers = serverService.findByIdIn(serverIds);
        return servers;
    }

    @Transactional
    public void updateStreamStatuses(String token, List<StreamDetailsView> statuses) {
        serverService.findByServerToken(token).ifPresent(server -> {
            for (var status : statuses) {
                var streamInstance = serverService.findStreamServerById(new StreamServerId(status.getStreamId(), server.getId()));
                if (streamInstance.isPresent()) {
                    var streamServer = streamInstance.get();
                    if (streamServer.getStreamDetails() == null) {
                        streamServer.setStreamDetails(new StreamDetails());
                    }
                    copyProperties(streamMapper.convertToEntity(status), streamServer.getStreamDetails(), "id");
                    serverService.saveStreamServer(streamServer);
                }
            }
        });
    }
}
