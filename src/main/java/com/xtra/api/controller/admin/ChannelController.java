package com.xtra.api.controller.admin;

import com.xtra.api.model.EpgChannelId;
import com.xtra.api.projection.admin.StreamInputPair;
import com.xtra.api.projection.admin.channel.*;
import com.xtra.api.service.admin.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;

    @Autowired
    public ChannelController(ChannelService channelService) {
        this.channelService = channelService;
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
    @GetMapping("/play/{stream_token}/{line_token}")
    public ResponseEntity<String> playChannel(@PathVariable String stream_token, @PathVariable String line_token, HttpServletRequest request) {
        String playlist = channelService.playChannel(stream_token, line_token, request);
        HttpHeaders responseHeaders = new HttpHeaders();
        return ResponseEntity.ok()
                .headers(responseHeaders).contentType(MediaType.valueOf("application/x-mpegurl"))
                .headers(responseHeaders).contentLength(Long.parseLong(String.valueOf(playlist.length())))
                .headers(responseHeaders).cacheControl(CacheControl.noCache())
                .headers(responseHeaders).cacheControl(CacheControl.noStore())
                .body(playlist);
    }

    @GetMapping("/{id}/change-source")
    public ResponseEntity<Integer> changeSource(@PathVariable Long id, @RequestParam String portNumber, HttpServletRequest request) {
        return ResponseEntity.ok(channelService.changeSource(id, portNumber, request));
    }

    @GetMapping("/{id}/info")
    public ResponseEntity<ChannelInfo> channelInfo(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getChannelInfo(id));
    }

    @PatchMapping("{id}/epg-channel")
    public ResponseEntity<?> setEpgRecord(@PathVariable Long id, @RequestBody EpgChannelId epgChannelId) {
        channelService.setEpgRecord(id,epgChannelId);
        return ResponseEntity.ok().build();
    }

    //Stream Tools
    @PatchMapping("/tools/dns")
    public ResponseEntity<?> changeDns(@RequestBody StreamInputPair streamInputPair){
        channelService.changeDns(streamInputPair);
        return ResponseEntity.ok().build();
    }

}
