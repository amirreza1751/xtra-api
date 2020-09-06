package com.xtra.api.controller;

import com.xtra.api.model.Stream;
import com.xtra.api.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;

import static org.springframework.beans.BeanUtils.copyProperties;

@RestController
@RequestMapping("/streams")
public class StreamController {
    StreamService streamService;

    @Autowired
    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping("/")
    public Page<Stream> getStreams(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return streamService.findAll(search, pageNo, pageSize, sortBy, sortDir);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Stream> getStream(@PathVariable Long id) {
        return ResponseEntity.ok(streamService.findByIdOrFail(id));
    }

    @PostMapping("/")
    public ResponseEntity<Stream> addStream(@RequestBody Stream stream) {
        return ResponseEntity.ok(streamService.add(stream));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Stream> updateStream(@PathVariable Long id, @RequestBody Stream stream) {
        return ResponseEntity.ok(streamService.updateOrFail(id, stream));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStream(@PathVariable Long id) {
        streamService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/stream_info/batch")
    @Transactional
    public ResponseEntity<?> batchUpdateStreamInfo(@RequestBody LinkedHashMap<String, Object> infos) {
        streamService.infoBatchUpdate(infos);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/get_id/{stream_token}")
    public ResponseEntity<Long> getStreamIdByToken(@PathVariable("stream_token") String streamToken) {
        return ResponseEntity.ok(streamService.findIdByToken(streamToken));
    }
}
