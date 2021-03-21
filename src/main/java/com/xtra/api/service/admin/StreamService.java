package com.xtra.api.service.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.projection.admin.ProgressInfoDto;
import com.xtra.api.projection.admin.StreamInfoDto;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.service.CrudService;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

public abstract class StreamService<S extends Stream, R extends StreamRepository<S>> extends CrudService<S, Long, R> {
    private final ServerService serverService;

    protected StreamService(R repository, String className, ServerService serverService) {
        super(repository, className);
        this.serverService = serverService;
    }

    public Optional<S> findById(Long id) {
        return repository.findById(id);
    }

    public Stream findByTokenOrFail(String token) {
        return repository.getByStreamToken(token).orElseThrow(() -> new EntityNotFoundException("Stream", token));
    }

    public Long findIdByToken(String token) {
        return findByTokenOrFail(token).getId();
    }


    public void infoBatchUpdate(LinkedHashMap<String, Object> infos, String portNumber, HttpServletRequest request) {
        Optional<Server> srv = serverService.findByIpAndCorePort(request.getRemoteAddr(), portNumber);
        Server server = new Server();
        if (srv.isPresent()) {
            server = srv.get();
        }
        ObjectMapper mapper = new ObjectMapper();
        List<StreamInfoDto> streamInfos = mapper.convertValue(infos.get("streamInfoList"), new TypeReference<>() {
        });
        List<ProgressInfoDto> progressInfos = mapper.convertValue(infos.get("progressInfoList"), new TypeReference<>() {
        });

        //amir
        StreamServer streamServer;
        for (StreamInfoDto streamInfoDto : streamInfos) {
            streamServer = serverService.findStreamServerById(new StreamServerId(streamInfoDto.getStreamId(), server.getId())).get();
            StreamInfo tempInfo = streamServer.getStreamInfo() != null ? streamServer.getStreamInfo() : new StreamInfo();
            copyProperties(streamInfoDto, tempInfo, "id", "streamServer", "streamId");
            streamServer.setStreamInfo(tempInfo);
            serverService.saveStreamServer(streamServer);
        }

        for (ProgressInfoDto progressInfoDto : progressInfos) {
            streamServer = serverService.findStreamServerById(new StreamServerId(progressInfoDto.getStreamId(), server.getId())).get();
            ProgressInfo tempProgress = streamServer.getProgressInfo() != null ? streamServer.getProgressInfo() : new ProgressInfo();
            copyProperties(progressInfoDto, tempProgress, "id", "streamServer", "streamId");
            streamServer.setProgressInfo(tempProgress);
            serverService.saveStreamServer(streamServer);
        }
        //amir
    }


    public boolean startOrFail(Long id, List<Long> serverIds) {
        for (Server server : getStreamServers(id, serverIds)) {
            serverService.sendStartRequest(id, server);
        }
        return true;
    }

    public boolean stopOrFail(Long id, List<Long> serverIds) {
        for (Server server : getStreamServers(id, serverIds)) {
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
        var stream = repository.findById(id).orElseThrow(entityNotFoundException("id", id));
        if (serverIds == null) {
            servers = stream.getStreamServers().stream().map(StreamServer::getServer).collect(Collectors.toList());
        } else servers = serverService.findByIdIn(serverIds);
        return servers;
    }
}
