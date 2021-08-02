package com.xtra.api.controller.admin;

import com.xtra.api.service.admin.MagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("mags")
public class MagController {
    private final MagService magService;

    public MagController(MagService magService) {
        this.magService = magService;
    }

    //system endpoint
    @CrossOrigin
    @GetMapping("/portal")
    public ResponseEntity<?> handlePortalRequest(
            @RequestParam("type") String requestType, @RequestParam("action") String requestAction, @RequestParam(value = "sn", required = false) String serialNumber,
            @RequestParam(value = "stb_type", required = false) String stbType, @RequestParam(value = "mac", required = false) String mac, @RequestParam(value = "ver", required = false) String version, @RequestParam(value = "image_version", required = false) String imageVersion,
            @RequestParam(value = "device_id", required = false) String deviceId, @RequestParam(value = "device_id2", required = false) String deviceId2, @RequestParam(value = "hw_version", required = false) String hwVersion, @RequestParam(value = "gmode", required = false) String gMode,
            HttpServletRequest req) {
        var userAgent = req.getHeader("HTTP_X_USER_AGENT");
        var userIp = req.getRemoteAddr();
        return magService.handlePortalRequest(userIp, userAgent, requestType, requestAction, serialNumber, mac, version, stbType, imageVersion, deviceId, deviceId2, hwVersion, gMode);
    }
}
