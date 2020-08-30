package com.xtra.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtra.api.exceptions.EntityNotFoundException;
import com.xtra.api.model.ProgressInfo;
import com.xtra.api.model.Stream;
import com.xtra.api.model.StreamInfo;
import com.xtra.api.repository.StreamInfoRepository;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequestMapping("/streams")
public class StreamController {
    StreamRepository streamRepository;
    StreamInfoRepository streamInfoRepository;
    StreamService streamService;

    @Autowired
    public StreamController(StreamRepository repository, StreamInfoRepository streamInfoRepository) {
        streamRepository = repository;
        this.streamInfoRepository = streamInfoRepository;
    }

    @GetMapping("/")
    public Page<Stream> getStreams(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return streamService.getAll(search, pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public Stream getStream(@PathVariable Long id) {
        return streamRepository.findById(id).orElseThrow(() -> new RuntimeException("Stream not found!"));
    }

    @PostMapping("/")
    public Stream addStream(@RequestBody Stream stream) {
        return streamRepository.save(stream);
    }

    @PatchMapping("/{id}")
    public Stream updateStream(@PathVariable Long id, @RequestBody Stream Stream) {
        if (streamRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException();
        }
        Stream.setId(id);
        return streamRepository.save(Stream);
    }

    @DeleteMapping("/{id}")
    public void deleteStream(@PathVariable Long id) {
        streamRepository.deleteById(id);
    }

    @PostMapping("/updateStreamInfo")
    @Transactional
    public void updateStreamInfo(@RequestBody LinkedHashMap<String, Object> infos) {
        ObjectMapper mapper = new ObjectMapper();
        List<StreamInfo> streamInfos = mapper.convertValue(infos.get("streamInfoList"), new TypeReference<>() {
        });
        List<ProgressInfo> progressInfos = mapper.convertValue(infos.get("progressInfoList"), new TypeReference<>() {
        });
        List<Stream> streams = streamRepository.findAllById(streamInfos.stream().mapToLong((StreamInfo::getStreamId)).boxed().collect(Collectors.toList()));

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

            streamRepository.save(stream);
        }

    }

    @GetMapping("/get_id/{stream_token}")
    public Long getStreamIdByToken(@PathVariable("stream_token") String streamToken) {
        var streamByToken = streamRepository.getByStreamToken(streamToken);
        return streamByToken.map(Stream::getId).orElse(null);
    }
}
