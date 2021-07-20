package com.xtra.api.controller.admin;

import com.xtra.api.projection.EntityListItem;
import com.xtra.api.service.admin.StreamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("streams")
public class StreamController {
    private final StreamService streamService;

    public StreamController(StreamService streamService) {
        this.streamService = streamService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<EntityListItem>> getStreamList(@RequestParam String search) {
        return ResponseEntity.ok(streamService.getStreamList(search));
    }

}
