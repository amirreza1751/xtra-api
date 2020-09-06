package com.xtra.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.ProgressInfo;
import com.xtra.api.model.Stream;
import com.xtra.api.model.StreamInfo;
import com.xtra.api.repository.StreamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@Service
public class StreamService extends CrudService<Stream, Long, StreamRepository> {

    public StreamService(StreamRepository streamRepository) {
        super(streamRepository, Stream.class);
    }

    public Optional<Stream> findById(Long streamId) {
        return repository.findById(streamId);
    }

    public Stream findByToken(String token) {
        return repository.getByStreamToken(token).orElseThrow(() -> new EntityNotFoundException("Stream", token));
    }

    public Long findIdByToken(String token) {
        return findByToken(token).getId();
    }

    @Override
    protected Page<Stream> findWithSearch(Pageable page, String search) {
        return null;
    }

    public void infoBatchUpdate(LinkedHashMap<String, Object> infos) {
        ObjectMapper mapper = new ObjectMapper();
        List<StreamInfo> streamInfos = mapper.convertValue(infos.get("streamInfoList"), new TypeReference<>() {
        });
        List<ProgressInfo> progressInfos = mapper.convertValue(infos.get("progressInfoList"), new TypeReference<>() {
        });
        List<Stream> streams = repository.findAllById(streamInfos.stream().mapToLong((StreamInfo::getStreamId)).boxed().collect(Collectors.toList()));

        for (Stream stream : streams) {
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
}
