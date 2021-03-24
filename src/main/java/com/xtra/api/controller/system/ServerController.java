package com.xtra.api.controller.system;

import com.xtra.api.model.ChannelList;
import com.xtra.api.projection.admin.channel.ChannelStart;
import com.xtra.api.service.system.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<ChannelStart> getChannel(@PathVariable Long channelId, HttpServletRequest request) {
        return ResponseEntity.ok(serverService.getChannelForServer(request, channelId));
    }

    @GetMapping("/channels")
    public ResponseEntity<ChannelList> getBatchChannel(HttpServletRequest request) {
        return ResponseEntity.ok(serverService.getAllChannelsForServer(request));
    }
}
