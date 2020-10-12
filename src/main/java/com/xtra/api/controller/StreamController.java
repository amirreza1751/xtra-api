package com.xtra.api.controller;

import com.xtra.api.model.Stream;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.service.StreamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.*;



public class StreamController {
    StreamService<Stream, StreamRepository<Stream>> streamService;

    public StreamController(StreamService<Stream,StreamRepository<Stream>> streamService) {
        this.streamService = streamService;
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
