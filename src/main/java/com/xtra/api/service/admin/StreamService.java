package com.xtra.api.service.admin;

import com.xtra.api.mapper.system.StreamMapper;
import com.xtra.api.model.exception.EntityNotFoundException;
import com.xtra.api.model.server.Server;
import com.xtra.api.model.stream.Stream;
import com.xtra.api.model.stream.StreamDetails;
import com.xtra.api.model.stream.StreamServer;
import com.xtra.api.model.stream.StreamServerId;
import com.xtra.api.projection.admin.channel.ChannelStart;
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


    public void stopStreamOnServers(Long id, List<Long> serverIds) {
        var channel = findByIdOrFail(id);
        for (Server server : getServersForStream(channel, serverIds)) {
            Set<StreamServer> streamServers = channel.getStreamServers();
            for (StreamServer streamServer : streamServers) {
                if (streamServer.getServer().getId().equals(server.getId())) {
                    streamServer.setStreamDetails(new StreamDetails());
                }
            }
            channel.setStreamServers(streamServers);
            repository.save(channel);
            serverService.sendStopRequest(id, server);
        }
    }

    public boolean restartOrFail(Long id, List<Long> serverIds) {
        var stream = findByIdOrFail(id);
        for (Server server : getServersForStream(stream, serverIds)) {
            serverService.sendRestartRequest(id, server);
        }
        return true;
    }

    protected List<Server> getServersForStream(Stream stream, List<Long> serverIds) {
        if (serverIds == null || serverIds.size() == 0)
            return stream.getStreamServers().stream().map(StreamServer::getServer).collect(Collectors.toList());
        return stream.getStreamServers().stream().map(StreamServer::getServer)
                .filter(server -> serverIds.contains(server.getId())).collect(Collectors.toList());
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
