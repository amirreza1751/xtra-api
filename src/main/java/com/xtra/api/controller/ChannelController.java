package com.xtra.api.controller;

import com.xtra.api.mapper.ChannelMapper;
import com.xtra.api.model.Channel;
import com.xtra.api.projection.ChannelView;
import com.xtra.api.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/channels")
public class ChannelController {
    private final ChannelService channelService;
    private final ChannelMapper channelMapper;

    @Autowired
    public ChannelController(ChannelService channelService, ChannelMapper channelMapper) {
        this.channelService = channelService;
        this.channelMapper = channelMapper;
    }

    // Stream CRUD
    @GetMapping("")
    public ResponseEntity<Page<ChannelView>> getChannels(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        var result = channelService.findAll(search, pageNo, pageSize, sortBy, sortDir);
        return ResponseEntity.ok(new PageImpl<>(result.stream().map(channelMapper::convertToDto).collect(Collectors.toList())));
    }

    @PostMapping(value = {"", "/{start}"})
    public ResponseEntity<ChannelView> addChannel(@Valid @RequestBody ChannelView channelView, @PathVariable(required = false) boolean start) {
        return ResponseEntity.ok(channelMapper.convertToDto(channelService.add(channelMapper.convertToEntity(channelView), channelView.getServers(), channelView.getCollections(), start)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChannelView> getChannelById(@PathVariable Long id) {
        return ResponseEntity.ok(channelMapper.convertToDto(channelService.findByIdOrFail(id)));
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
    @GetMapping("/start/{id}/")
    public ResponseEntity<String> startChannel(@PathVariable Long id, @RequestParam Long serverId) {
        if (channelService.startOrFail(id, serverId))
            return ResponseEntity.ok().build();
        else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/stop/{id}")
    public ResponseEntity<?> stopChannel(@PathVariable Long id, @RequestParam Long serverId) {
        if (channelService.stopOrFail(id, serverId))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/restart/{id}")
    public ResponseEntity<String> restartChannel(@PathVariable Long id, @RequestParam Long serverId) {
        if (channelService.restartOrFail(id, serverId))
            return ResponseEntity.ok().build();
        else
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PostMapping("/stream_info/batch")
    public ResponseEntity<?> batchUpdateStreamInfo(@RequestBody LinkedHashMap<String, Object> infos, @RequestParam String portNumber, HttpServletRequest request) {
        channelService.infoBatchUpdate(infos, portNumber, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
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
    public ResponseEntity<String> playChannel(@PathVariable String stream_token, @PathVariable String line_token, HttpServletRequest request){
        String playlist = channelService.playChannel(stream_token, line_token, request);
        HttpHeaders responseHeaders = new HttpHeaders();
        return ResponseEntity.ok()
                .headers(responseHeaders).contentType(MediaType.valueOf("application/x-mpegurl"))
                .headers(responseHeaders).contentLength(Long.parseLong(String.valueOf(playlist.length())))
                .headers(responseHeaders).cacheControl(CacheControl.noCache())
                .headers(responseHeaders).cacheControl(CacheControl.noStore())
                .body(playlist);
    }

}
