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
        return ResponseEntity.ok(lineService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Line> getLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.findByIdOrFail(id));
    }

    @PostMapping("")
    public ResponseEntity<Line> addLine(@RequestBody @Valid Line line) {
        return ResponseEntity.ok(lineService.add(line));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Line> updateLine(@PathVariable Long id, @RequestBody @Valid Line line) {
        return ResponseEntity.ok(lineService.updateOrFail(id, line));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLine(@PathVariable Long id) {
        lineService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/block/{blocked}")
    public ResponseEntity<?> setLineBlocked(@PathVariable Long id, @PathVariable boolean blocked) {
        lineService.updateLineBlock(id, blocked);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/ban/{banned}")
    public ResponseEntity<?> setLineBanned(@PathVariable Long id, @PathVariable boolean banned) {
        lineService.updateLineBan(id, banned);
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
