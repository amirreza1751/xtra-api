package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.ChangingServerPair;
import com.xtra.api.projection.admin.StreamInputPair;
import com.xtra.api.projection.admin.channel.*;
import com.xtra.api.projection.admin.epg.EpgDetails;
import com.xtra.api.service.admin.ChannelService;
import com.xtra.api.service.admin.StreamServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/channels")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class ChannelController {
    private final ChannelService channelService;
    private final StreamServerService streamServerService;

    @Autowired
    public ChannelController(ChannelService channelService, StreamServerService streamServerService) {
        this.channelService = channelService;
        this.streamServerService = streamServerService;
    }

    // Stream CRUD
    @PreAuthorize("hasAnyAuthority({'channels_manage'}) or hasRole('SUPER_ADMIN')")
    @GetMapping("")
    public ResponseEntity<Page<ChannelInfo>> getChannels(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(channelService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'channels_manage', 'channels_add'}) or hasRole('SUPER_ADMIN')")
    @PostMapping(value = {"", "/{start}"})
    public ResponseEntity<ChannelView> addChannel(@RequestBody ChannelInsertView insertView, @PathVariable(required = false) boolean start) {
        return ResponseEntity.ok(channelService.add(insertView, start));
    }

    @PreAuthorize("hasAnyAuthority({'channels_manage'}) or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ChannelView> getChannelById(@PathVariable Long id) {
        return ResponseEntity.ok(channelService.getViewById(id));
    }

    @PreAuthorize("hasAnyAuthority({'channels_manage', 'channels_edit'}) or hasRole('SUPER_ADMIN')")
    @PatchMapping(value = {"/{id}", "/{id}/{restart}"})
    public ResponseEntity<ChannelView> updateChannel(@PathVariable Long id, @RequestBody ChannelInsertView channelView, @PathVariable(required = false) boolean restart) {
        return ResponseEntity.ok(channelService.save(id, channelView, restart));
    }

    @PreAuthorize("hasAnyAuthority({'channels_manage', 'channels_delete'}) or hasRole('SUPER_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChannel(@PathVariable Long id) {
        channelService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Batch Actions
    @PreAuthorize("hasAnyAuthority({'channels_manage', 'channels_batch_edit'}) or hasRole('SUPER_ADMIN')")
    @PatchMapping(value = {"/batch", "/batch/{restart}"})
    public ResponseEntity<?> updateChannels(@RequestBody ChannelBatchInsertView channelView, @PathVariable(required = false) boolean restart) {
        channelService.saveAll(channelView, restart);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority({'channels_manage', 'channels_batch_delete'}) or hasRole('SUPER_ADMIN')")
    @DeleteMapping("/batch")
    public ResponseEntity<?> deleteChannels(@RequestBody ChannelBatchDeleteView channelView) {
        channelService.deleteAll(channelView);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAnyAuthority({'channels_manage', 'channels_import'}) or hasRole('SUPER_ADMIN')")
    @RequestMapping(path = "/import", method = RequestMethod.POST, consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> importMovies(@ModelAttribute ChannelImportView importView) {
        channelService.importChannels(importView);
        return ResponseEntity.ok().build();
    }

    // Stream Operations
    @PreAuthorize("hasAnyAuthority({'channels_manage'}) or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}/start")
    public ResponseEntity<String> startChannelOnServers(@PathVariable Long id, @RequestParam(required = false) Set<Long> serversIds) {
        channelService.startStreamOnServers(id, serversIds);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority({'channels_manage'}) or hasRole('SUPER_ADMIN')")
    @GetMapping("/{id}/stop")
    public ResponseEntity<?> stopChannelOnServers(@PathVariable Long id, @RequestParam(required = false) List<Long> servers) {
        channelService.stopStreamOnServers(id, servers);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<?> setEpgRecord(@PathVariable Long id, @RequestBody EpgDetails epgDetails) {
        channelService.setEpgRecord(id, epgDetails);
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
