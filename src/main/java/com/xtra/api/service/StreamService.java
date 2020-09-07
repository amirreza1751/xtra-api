package com.xtra.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.ProgressInfo;
import com.xtra.api.model.Stream;
import com.xtra.api.model.StreamInfo;
import com.xtra.api.repository.StreamRepository;

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


    public void infoBatchUpdate(LinkedHashMap<String, Object> infos) {
        ObjectMapper mapper = new ObjectMapper();
        List<StreamInfo> streamInfos = mapper.convertValue(infos.get("streamInfoList"), new TypeReference<>() {
        });
        List<ProgressInfo> progressInfos = mapper.convertValue(infos.get("progressInfoList"), new TypeReference<>() {
        });
        List<S> streams = repository.findAllById(streamInfos.stream().map(StreamInfo::getStreamId).collect(Collectors.toList()));

        for (S stream : streams) {
            var streamInfo = streamInfos.stream().filter((info) -> info.getStreamId().equals(stream.getId())).findAny();
            if (streamInfo.isPresent()) {
                StreamInfo newInfo = streamInfo.get();
                StreamInfo infoEntity;
                infoEntity = stream.getStreamInfo() != null ? stream.getStreamInfo() : new StreamInfo();
                copyProperties(newInfo, infoEntity, "id");
                stream.setStreamInfo(infoEntity);
            }


            var progressInfo = progressInfos.stream().filter((info) -> info.getStreamId().equals(stream.getId())).findAny();
            if (progressInfo.isPresent()) {
                ProgressInfo newInfo = progressInfo.get();
                ProgressInfo infoEntity;
                infoEntity = stream.getProgressInfo() != null ? stream.getProgressInfo() : new ProgressInfo();
                copyProperties(newInfo, infoEntity, "id");
                stream.setProgressInfo(infoEntity);
            }

            repository.save(stream);
        }
    }

    public boolean startOrFail(Long id) {
        Optional<S> channel = repository.findById(id);
        if (channel.isPresent()) {
            return serverService.sendStartRequest(id);
        } else
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
    }

    public boolean stopOrFail(Long id) {
        Optional<S> streamById = repository.findById(id);
        if (streamById.isPresent()) {
            S stream = streamById.get();
            if (!serverService.sendStopRequest(stream.getId()))
                return false;
            stream.setStreamInfo(null);
            stream.setProgressInfo(null);
            repository.save(stream);
            return true;
        } else
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
    }

    public boolean restartOrFail(Long id) {
        Optional<S> streamById = repository.findById(id);
        if (streamById.isPresent()) {
            S stream = streamById.get();
            serverService.sendRestartRequest(stream.getId());
            return true;
        } else
            throw new EntityNotFoundException(aClass.getSimpleName(), id.toString());
    }
}
