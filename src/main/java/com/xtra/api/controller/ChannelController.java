package com.xtra.api.controller;

import com.xtra.api.facade.ChannelFacade;
import com.xtra.api.model.Channel;
import com.xtra.api.projection.ChannelDTO;
import com.xtra.api.service.ChannelService;
import com.xtra.api.service.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    ChannelService channelService;
    StreamService streamService;
    private final ChannelFacade channelFacade;

    @Autowired
    public ChannelController(ChannelService channelService, StreamService streamService, ChannelFacade channelFacade) {
        this.channelService = channelService;
        this.streamService = streamService;
        this.channelFacade = channelFacade;
    }

    // Stream CRUD
    @GetMapping("")
    public ResponseEntity<Page<Channel>> getChannels(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = channelService.findAll(search, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(result);
    }

//    @PostMapping(value = {"", "/{start}"})
//    public ResponseEntity<Channel> addChannel(@Valid @RequestBody Channel channel, @PathVariable(required = false) boolean start) {
//        return ResponseEntity.ok(channelService.addChannel(channel, start));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Channel> getChannelById(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.findByIdOrFail(id));
    }

    @PatchMapping(value = {"/{id}", "/{id}/{restart}"})
    public ResponseEntity<Channel> updateChannel(@PathVariable Long id, @RequestBody Channel channel, @PathVariable(required = false) boolean restart) {
        var result = channelService.updateChannel(id, channel, restart);
        return result.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannel(@PathVariable Long id) {
        channelService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Stream Operations
    @GetMapping("/start/{id}")
    public ResponseEntity<String> startChannel(@PathVariable Long id) {
        if (channelService.startOrFail(id))
            return ResponseEntity.ok().build();
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/stop/{id}")
    public ResponseEntity<?> stopChannel(@PathVariable Long id) {
        if (channelService.stopOrFail(id))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/restart/{id}")
    public ResponseEntity<String> restartChannel(@PathVariable Long id) {
        if (channelService.restartOrFail(id))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/stream_info/batch")
    public ResponseEntity<?> batchUpdateStreamInfo(@RequestBody LinkedHashMap<String, Object> infos) {
        channelService.infoBatchUpdate(infos);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/get_id/{stream_token}")
    public ResponseEntity<Long> getStreamIdByToken(@PathVariable("stream_token") String streamToken) {
        return ResponseEntity.ok(channelService.findIdByToken(streamToken));
    }

    static class ChannelAndServerIds {
        public Channel channel;
        public ArrayList<Long> serverIds;
        public ChannelAndServerIds(){}
        public ChannelAndServerIds(Channel channel , ArrayList<Long> serverIds){
            this.channel = channel;
            this.serverIds = serverIds;
        }
    }

    @PostMapping(value = {"customized-insert", "customized-insert/{start}"})
    public ResponseEntity<Channel> add(@RequestBody ChannelAndServerIds channelAndServerIds, @PathVariable(required = false) boolean start) {
        return ResponseEntity.ok(channelService.add( channelAndServerIds.channel, channelAndServerIds.serverIds, start));
    }

    @PostMapping("/{channel_id}/update-servers-list")
    public void updateServersList(@PathVariable Long channel_id, @RequestBody Long[] serverIds){
        channelService.updateServersList(channel_id, serverIds);
    }

}
