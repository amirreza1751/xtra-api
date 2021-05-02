package com.xtra.api.controller.admin;

import com.xtra.api.model.EpgChannelId;
import com.xtra.api.projection.admin.ChangingServerPair;
import com.xtra.api.projection.admin.StreamInputPair;
import com.xtra.api.projection.admin.channel.*;
import com.xtra.api.service.admin.ChannelService;
import com.xtra.api.service.admin.StreamServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final StreamServerService streamServerService;

    @Autowired
    public ChannelController(ChannelService channelService, StreamServerService streamServerService) {
        this.channelService = channelService;
        this.streamServerService = streamServerService;
    }

    // Stream CRUD
    @GetMapping("")
    public ResponseEntity<Page<ChannelInfo>> getChannels(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(channelService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PostMapping(value = {"", "/{start}"})
    public ResponseEntity<ChannelView> addChannel(@RequestBody ChannelInsertView insertView, @PathVariable(required = false) boolean start) {
        return ResponseEntity.ok(channelService.add(insertView, start));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelView> getChannelById(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getViewById(id));
    }

    @PatchMapping(value = {"/{id}", "/{id}/{restart}"})
    public ResponseEntity<ChannelView> updateChannel(@PathVariable Long id, @RequestBody ChannelInsertView channelView, @PathVariable(required = false) boolean restart) {
        return ResponseEntity.ok(channelService.save(id, channelView, restart));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannel(@PathVariable Long id) {
        channelService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Batch Actions
    @PatchMapping(value = {"/batch", "/batch/{restart}"})
    public ResponseEntity<?> updateChannels(@RequestBody ChannelBatchInsertView channelView, @PathVariable(required = false) boolean restart) {
        channelService.saveAll(channelView, restart);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteChannels(@RequestBody ChannelBatchDeleteView channelView) {
        channelService.deleteAll(channelView);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Stream Operations
    @GetMapping("/{id}/start")
    public ResponseEntity<String> startChannel(@PathVariable Long id, @RequestParam(required = false) List<Long> servers) {
        if (channelService.startOrFail(id, servers))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{id}/stop")
    public ResponseEntity<?> stopChannel(@PathVariable Long id, @RequestParam(required = false) List<Long> servers) {
        if (channelService.stopOrFail(id, servers))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/{id}/restart")
    public ResponseEntity<String> restartChannel(@PathVariable Long id, @RequestParam(required = false) List<Long> servers) {
        if (channelService.restartOrFail(id, servers))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/get_id/{stream_token}")
    public ResponseEntity<Long> getStreamIdByToken(@PathVariable("stream_token") String streamToken) {
        return ResponseEntity.ok(channelService.findIdByToken(streamToken));
    }


    @PostMapping("/{channel_id}/update-servers-list")
    public void updateServersList(@PathVariable Long channel_id, @RequestBody Long[] serverIds) {
        channelService.updateServersList(channel_id, serverIds);
    }

    //Play a Channel
    @GetMapping("/play/{line_token}/{stream_token}")
    public void playChannel(@PathVariable String stream_token, @PathVariable String line_token, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String channelLink = channelService.playChannel(stream_token, line_token, request);
        response.sendRedirect(channelLink);
    }

    @GetMapping("/{id}/change-source")
    public ResponseEntity<Integer> changeSource(@PathVariable Long id, @RequestHeader(value = "token", required = false) String token) {
        if (token == null)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(channelService.changeSource(id, token));
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<ChannelInfo> channelInfo(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getChannelInfo(id));
    }

    @PatchMapping("{id}/epg-channel")
    public ResponseEntity<?> setEpgRecord(@PathVariable Long id, @RequestBody EpgChannelId epgChannelId) {
        channelService.setEpgRecord(id, epgChannelId);
        return ResponseEntity.ok().build();
    }

    //Stream Tools
    @PatchMapping("/tools/dns")
    public ResponseEntity<?> changeDns(@RequestBody StreamInputPair streamInputPair) {
        channelService.changeDns(streamInputPair);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/tools/server")
    public ResponseEntity<?> changeServer(@RequestBody ChangingServerPair changingServerPair) {
        streamServerService.changeServer(changingServerPair);
        return ResponseEntity.ok().build();
    }
}
