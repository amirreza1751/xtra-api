package com.xtra.api.controller.system;

import com.xtra.api.model.line.LineStatus;
import com.xtra.api.projection.system.LineAuth;
import com.xtra.api.service.system.SystemLineServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system")
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


}
