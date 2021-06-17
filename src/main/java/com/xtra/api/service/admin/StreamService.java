package com.xtra.api.service.admin;

import com.xtra.api.mapper.system.StreamMapper;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.*;
import com.xtra.api.projection.system.StreamDetailsView;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.repository.StreamServerRepository;
import com.xtra.api.service.CrudService;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

public abstract class StreamService<S extends Stream, R extends StreamRepository<S>> extends CrudService<S, Long, R> {
    private final ServerService serverService;
    private final StreamMapper streamMapper;
    private final StreamServerRepository streamServerRepository;

    protected StreamService(R repository, String className, ServerService serverService, StreamMapper streamMapper, StreamServerRepository streamServerRepository) {
        super(repository, className);
        this.serverService = serverService;
        this.streamMapper = streamMapper;
        this.streamServerRepository = streamServerRepository;
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

    @Transactional
    public void updateStreamStatuses(String token, List<StreamDetailsView> statuses) {
        serverService.findByServerToken(token).ifPresent(server -> {
            for (var streamServer : server.getStreamServers()) {
                var status = statuses.stream().filter(details -> details.getStreamId().equals(streamServer.getId().getStreamId())).findFirst();
                if (status.isPresent()) {
                    copyProperties(streamMapper.convertToEntity(status.get()), streamServer.getStreamDetails(), "id");
                    streamServer.getStreamDetails().setStreamStatus(StreamStatus.ONLINE);
                } else {
                    streamServer.setStreamDetails(new StreamDetails());
                    streamServer.getStreamDetails().setStreamStatus(StreamStatus.OFFLINE);
                }
                serverService.saveStreamServer(streamServer);
            }
        });
    }
}
