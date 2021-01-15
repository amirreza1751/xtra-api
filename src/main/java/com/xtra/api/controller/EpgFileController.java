package com.xtra.api.controller;

import com.xtra.api.model.Channel;
import com.xtra.api.model.EpgChannel;
import com.xtra.api.model.EpgFile;
import com.xtra.api.service.EpgFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/epg")
public class EpgFileController {

    private final EpgFileService epgFileService;

    @Autowired
    public EpgFileController(EpgFileService epgFileService){
        this.epgFileService = epgFileService;
    }

    @GetMapping("")
    public ResponseEntity<Page<EpgFile>> getAll(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(epgFileService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EpgFile> getEpgFile(@PathVariable Long id) {
        return ResponseEntity.ok(epgFileService.findByIdOrFail(id));
    }

    @PostMapping("")
    public ResponseEntity<EpgFile> addEpgFile(@RequestBody EpgFile epgFile) {
        return ResponseEntity.ok(epgFileService.add(epgFile));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<EpgFile> updateEpgFile(@PathVariable Long id, @RequestBody EpgFile epgFile) {
        return ResponseEntity.ok(epgFileService.updateEpgFile(id, epgFile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEpgFile(@PathVariable Long id) {
        epgFileService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/epg-channels/{epgChannelId}/streams/{streamId}")
    public ResponseEntity<Channel> addEpgChannelToStream(@PathVariable Long epgChannelId, @PathVariable Long streamId) {
        return ResponseEntity.ok(epgFileService.addEpgChannelToStream(epgChannelId, streamId));
    }

    @GetMapping("/{id}/epg-channels")
    public ResponseEntity<Set<EpgChannel>> getEpgChannels(@PathVariable Long id) {
        return ResponseEntity.ok(epgFileService.getEpgChannels(id));
    }
}
