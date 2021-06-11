package com.xtra.api.controller.admin;

import com.xtra.api.model.stream.Stream;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.service.admin.StreamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


public class StreamController {
    StreamService<Stream, StreamRepository<Stream>> streamService;

    public StreamController(StreamService<Stream,StreamRepository<Stream>> streamService) {
        this.streamService = streamService;
    }

    @GetMapping("/get_id/{stream_token}")
    public ResponseEntity<Long> getStreamIdByToken(@PathVariable("stream_token") String streamToken) {
        return ResponseEntity.ok(streamService.findIdByToken(streamToken));
    }

}
