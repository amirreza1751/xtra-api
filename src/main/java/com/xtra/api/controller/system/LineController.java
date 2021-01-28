package com.xtra.api.controller.system;

import com.xtra.api.model.LineStatus;
import com.xtra.api.projection.line.LineAuth;
import com.xtra.api.service.system.SystemLineServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class LineController {

    private final SystemLineServiceImpl lineService;

    public LineController(SystemLineServiceImpl lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/stream_auth")
    public LineStatus authorizeLineForStream(@RequestBody LineAuth lineAuth) {
        return lineService.isLineEligibleForPlaying(lineAuth);
    }

    @PostMapping("/vod_auth")
    public LineStatus authorizeLineForVod(@RequestBody LineAuth lineAuth) {
        return lineService.isLineEligibleForPlaying(lineAuth);
    }

    @GetMapping("/get_id/{line_token}")
    public ResponseEntity<Long> getLineByToken(@PathVariable("line_token") String lineToken) {
        return ResponseEntity.ok(lineService.getLineIdByToken(lineToken));
    }
}
