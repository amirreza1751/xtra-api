package com.xtra.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.*;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.repository.StreamServerRepository;
import org.checkerframework.checker.nullness.Opt;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

public abstract class StreamService<S extends Stream, R extends StreamRepository<S>> extends CrudService<S, Long, R> {
    private final ServerService serverService;

    protected StreamService(R repository, Class<S> aClass, ServerService serverService) {
        super(repository, aClass);
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
        if (srv.isPresent()){
            server = srv.get();
        }
        ObjectMapper mapper = new ObjectMapper();
        List<StreamInfo> streamInfos = mapper.convertValue(infos.get("streamInfoList"), new TypeReference<>() {
        });
        List<ProgressInfo> progressInfos = mapper.convertValue(infos.get("progressInfoList"), new TypeReference<>() {
        });

        //amir
        StreamServer streamServer;
        for (StreamInfo streamInfo : streamInfos){
            streamServer = serverService.findStreamServerById(new StreamServerId(streamInfo.getStreamId(), server.getId())).get();
            StreamInfo tempInfo = streamServer.getStreamInfo() != null ? streamServer.getStreamInfo() : new StreamInfo();
            copyProperties(streamInfo, tempInfo, "id", "streamServer");
            streamServer.setStreamInfo(tempInfo);
            serverService.saveStreamServer(streamServer);
        }


        for (ProgressInfo progressInfo : progressInfos){
            streamServer = serverService.findStreamServerById(new StreamServerId(progressInfo.getStreamId() , server.getId())).get();
            ProgressInfo tempProgress = streamServer.getProgressInfo() != null ? streamServer.getProgressInfo() : new ProgressInfo();
            copyProperties(progressInfo, tempProgress, "id", "streamServer");
            streamServer.setProgressInfo(tempProgress);
            serverService.saveStreamServer(streamServer);
        }
        //amir

    }

    public boolean startOrFail(Long id, Long serverId) {
        Optional<S> ch = repository.findById(id);
        if (ch.isPresent()) {
            S channel = ch.get();
            if (!channel.getStreamServers().contains(new StreamServer(new StreamServerId(id, serverId)))){
                throw new EntityNotFoundException(StreamServer.class.getSimpleName(), new StreamServerId(id, serverId).toString());
            }
            Server server = serverService.findByIdOrFail(serverId);
            return serverService.sendStartRequest(id, server);
        } else
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
    }

    public boolean stopOrFail(Long id, Long serverId) {
        Optional<S> streamById = repository.findById(id);
        if (streamById.isPresent()) {
            S stream = streamById.get();
            if (!stream.getStreamServers().contains(new StreamServer(new StreamServerId(id, serverId)))){
                throw new EntityNotFoundException(Server.class.getSimpleName(), serverId.toString());
            }
            Server server = serverService.findByIdOrFail(serverId);
            if (!serverService.sendStopRequest(stream.getId(), server))
                return false;
//            stream.setStreamInfo(null);
//            stream.setProgressInfo(null);
            repository.save(stream);
            return true;
        } else
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
    }

    public boolean restartOrFail(Long id, Long serverId) {
        Optional<S> streamById = repository.findById(id);
        if (streamById.isPresent()) {
            S stream = streamById.get();
            if (!stream.getStreamServers().contains(new StreamServer(new StreamServerId(id, serverId)))){
                throw new EntityNotFoundException(Server.class.getSimpleName(), serverId.toString());
            }
            Server server = serverService.findByIdOrFail(serverId);
            serverService.sendRestartRequest(stream.getId(), server);
            return true;
        } else
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
    }

}
