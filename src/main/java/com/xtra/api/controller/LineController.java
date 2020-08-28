package com.xtra.api.controller;

import com.xtra.api.model.Line;
import com.xtra.api.model.LineActivity;
import com.xtra.api.model.LineActivityId;
import com.xtra.api.model.LineStatus;
import com.xtra.api.repository.LineActivityRepository;
import com.xtra.api.repository.StreamRepository;
import com.xtra.api.service.LineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/lines")
public class LineController {
    LineService lineService;

    @Autowired
    public LineController(LineService lineService) {
        this.lineService = lineService;
    }

    @GetMapping("")
    public ResponseEntity<Page<Line>> getLines(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = lineService.getLines(pageNo, pageSize, search, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public Line getLine(@PathVariable Long id) {
        return lineService.getLine(id);
    }

    @PostMapping("")
    public ResponseEntity<Line> addLine(@RequestBody @Valid Line line) {
        return ResponseEntity.ok(lineService.addLine(line));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Line> updateLine(@PathVariable Long id, @RequestBody @Valid Line line) {
        return ResponseEntity.ok(lineService.updateLine(id, line));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLine(@PathVariable Long id) {
        lineService.deleteLine(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/block")
    public ResponseEntity<?> blockLine(@PathVariable Long id) {
        lineService.blockLine(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/unblock")
    public ResponseEntity<?> unblockLine(@PathVariable Long id) {
        lineService.unblockLine(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/ban")
    public ResponseEntity<?> banLine(@PathVariable Long id) {
        lineService.banLine(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/unban")
    public ResponseEntity<?> unbanLine(@PathVariable Long id) {
        lineService.unbanLine(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/kill_connections/{id}")
    public ResponseEntity<String> killLineConnections(@PathVariable Long id) {
        lineService.killAllConnections(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/stream_auth/{line_token}/{stream_token}")
    public LineStatus authorizeLineForStream(@PathVariable("line_token") String lineToken, @PathVariable("stream_token") String streamToken) {
        return lineService.isLineEligibleForPlaying(lineToken, streamToken);
    }

    @GetMapping("/vod_auth/{line_token}/{vod_token}")
    public LineStatus authorizeLineForVod(@PathVariable("line_token") String lineToken, @PathVariable("vod_token") String vodToken) {
        return lineService.isLineEligibleForPlaying(lineToken, vodToken);
    }

    @GetMapping("/get_id/{line_token}")
    public ResponseEntity<Long> getLineByToken(@PathVariable("line_token") String lineToken) {
        return ResponseEntity.ok(lineService.getLineIdByToken(lineToken));
    }

}
