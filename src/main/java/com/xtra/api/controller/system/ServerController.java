package com.xtra.api.controller.system;

import com.xtra.api.model.ChannelList;
import com.xtra.api.projection.admin.channel.ChannelStart;
import com.xtra.api.service.system.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("systemServerController")
@RequestMapping("/system/server")
public class ServerController {

    private final ServerService serverService;

    @Autowired
    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    //Fetch channel details to start streaming (request origin: core)
    @GetMapping("/channels/{channelId}")
    public ResponseEntity<ChannelStart> getChannel(@PathVariable Long channelId, @RequestHeader(value = "token", required = false) String token) {
        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(serverService.getChannelForServer(channelId, token));
    }

    @GetMapping("/channels")
    public ResponseEntity<ChannelList> getBatchChannel(@RequestHeader(value = "token", required = false) String token) {
        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(serverService.getAllChannelsForServer(token));
    }
}
