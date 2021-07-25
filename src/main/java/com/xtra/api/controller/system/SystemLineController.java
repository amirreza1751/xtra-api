package com.xtra.api.controller.system;

import com.xtra.api.model.line.LineStatus;
import com.xtra.api.projection.system.LineAuth;
import com.xtra.api.service.system.SystemLineServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class SystemLineController {

    private final SystemLineServiceImpl lineService;

    public SystemLineController(SystemLineServiceImpl lineService) {
        this.lineService = lineService;
    }

    @PostMapping("/stream_auth")
    public LineStatus authorizeLineForStream(@RequestBody LineAuth lineAuth) {
        return lineService.canLinePlayStream(lineAuth);
    }

    @PostMapping("/vod_auth")
    public LineStatus authorizeLineForVod(@RequestBody LineAuth lineAuth) {
        return lineService.canLinePlayVod(lineAuth);
    }

    @GetMapping("/get_id/{line_token}")
    public ResponseEntity<Long> getLineByToken(@PathVariable("line_token") String lineToken) {
        return ResponseEntity.ok(lineService.getLineIdByToken(lineToken));
    }
}
