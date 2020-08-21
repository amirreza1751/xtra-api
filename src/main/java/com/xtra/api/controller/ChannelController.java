package com.xtra.api.controller;

import com.xtra.api.model.Channel;
import com.xtra.api.repository.ChannelRepository;
import com.xtra.api.repository.ServerRepository;
import com.xtra.api.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    ChannelService channelService;
    ChannelRepository channelRepository;
    ServerRepository serverRepository;

    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @Autowired
    public ChannelController(ChannelRepository channelRepository, ServerRepository serverRepository, ChannelService channelService) {
        this.channelRepository = channelRepository;
        this.serverRepository = serverRepository;
        this.channelService = channelService;
    }

    // Stream CRUD
    @GetMapping("")
    public ResponseEntity<Page<Channel>> getChannels(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = channelService.getChannels(pageNo, pageSize, search, sortBy, sortDir);
        return ResponseEntity.ok().body(result);
    }

    @PostMapping(value = {"", "/{restart}"})
    public ResponseEntity<Channel> addChannel(@Valid @RequestBody Channel channel, @PathVariable(required = false) boolean restart) {
        return ResponseEntity.ok(channelService.addChannel(channel, restart));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Channel> getChannelById(@PathVariable Long id) {
        var channel = channelService.findById(id);
        return channel.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping(value = {"/{id}", "/{id}/{restart}"})
    public ResponseEntity<Channel> updateChannel(@PathVariable Long id, @RequestBody Channel channel, @PathVariable(required = false) boolean restart) {
        var result = channelService.update(id, channel, restart);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannel(@PathVariable Long id) {
        channelService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Stream Operations
    @GetMapping("/start/{id}")
    public ResponseEntity<String> startChannel(@PathVariable Long id) {
        if (channelService.start(id))
            return ResponseEntity.ok().build();
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/stop/{id}")
    public ResponseEntity<?> stopChannel(@PathVariable Long id) {
        if (channelService.stop(id))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/restart/{id}")
    public ResponseEntity<String> restartChannel(@PathVariable Long id) {
        if (channelService.restart(id))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
