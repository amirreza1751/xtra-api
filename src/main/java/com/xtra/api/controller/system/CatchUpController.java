package com.xtra.api.controller.system;

import com.xtra.api.model.ChannelList;
import com.xtra.api.projection.admin.channel.ChannelStart;
import com.xtra.api.service.system.CatchUpService;
import com.xtra.api.service.system.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("systemCatchUpController")
@RequestMapping("/catch-up/")
public class CatchUpController {

    private final CatchUpService catchUpService;

    @Autowired
    public CatchUpController(CatchUpService catchUpService) {

        this.catchUpService = catchUpService;
    }

    @GetMapping("/streams/{id}/recording/{status}")
    public ResponseEntity<Void> updateRecordingStatus(@PathVariable Long id, @PathVariable boolean status, @RequestHeader(value = "token", required = false) String token) {
        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        catchUpService.updateRecordingStatus(token, status, id);
        return ResponseEntity.ok().build();
    }

}
