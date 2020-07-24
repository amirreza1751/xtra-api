package com.xtra.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtra.api.exceptions.EntityNotFound;
import com.xtra.api.model.ProgressInfo;
import com.xtra.api.model.Stream;
import com.xtra.api.model.StreamInfo;
import com.xtra.api.repository.StreamInfoRepository;
import com.xtra.api.repository.StreamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/streams")
public class StreamController {
    StreamRepository streamRepository;
    StreamInfoRepository streamInfoRepository;

    @Autowired
    public StreamController(StreamRepository repository, StreamInfoRepository streamInfoRepository) {
        streamRepository = repository;
        this.streamInfoRepository = streamInfoRepository;
    }

    @GetMapping("/")
    public Page<Stream> getStreams(@RequestParam(defaultValue = "0", name = "page_no") int page_no, @RequestParam(defaultValue = "25", name = "page_size") int page_size) {
        Pageable page = PageRequest.of(page_no, page_size);
        return streamRepository.findAll(page);
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
            throw new EntityNotFound();
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
    public void updateStreamInfo(@RequestBody Map<String, Object> infos) {
        ObjectMapper mapper = new ObjectMapper();
        List<StreamInfo> streamInfos = (List<StreamInfo>) infos.get("streamInfoList");
        List<ProgressInfo> progressInfos = (List<ProgressInfo>) infos.get("progressInfoList");
        List<Stream> streams = streamRepository.findAllById(streamInfos.stream().mapToLong((StreamInfo::getId)).boxed().collect(Collectors.toList()));

        for (StreamInfo info : streamInfos) {
            streamInfoRepository.save(info);
        }

    }
}
