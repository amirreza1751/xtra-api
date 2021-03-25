package com.xtra.api.controller.system;

import com.xtra.api.model.ChannelList;
import com.xtra.api.projection.admin.channel.ChannelStart;
import com.xtra.api.service.system.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController("systemServerController")
@RequestMapping("/servers/current/")
public class ServerController {

    private final ServerService serverService;

    @Autowired
    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    //Fetch channel details to start streaming (request origin: core)
    @GetMapping("/channels/{channelId}")
    public ResponseEntity<ChannelStart> getChannel(@PathVariable Long channelId, HttpServletRequest request, @RequestParam String port) {
        return ResponseEntity.ok(serverService.getChannelForServer(request, channelId, port));
    }

    @GetMapping("/channels")
    public ResponseEntity<ChannelList> getBatchChannel(HttpServletRequest request) {
        return ResponseEntity.ok(serverService.getAllChannelsForServer(request));
    }
}
