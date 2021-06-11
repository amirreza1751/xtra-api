package com.xtra.api.controller.admin;

import com.xtra.api.model.server.File;
import com.xtra.api.projection.admin.server.ServerInsertView;
import com.xtra.api.projection.admin.server.ServerView;
import com.xtra.api.projection.admin.server.SimpleServerView;
import com.xtra.api.projection.admin.server.resource.ResourceView;
import com.xtra.api.service.admin.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/servers")
public class ServerController {
    ServerService serverService;

    @Autowired
    public ServerController(ServerService serverService) {
        this.serverService = serverService;
    }

    @GetMapping("")
    public ResponseEntity<Page<SimpleServerView>> getServers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(serverService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerView> getServerById(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getById(id));
    }

    @PostMapping("")
    public ResponseEntity<ServerView> addServer(@Valid @RequestBody ServerInsertView insertView) {
        return ResponseEntity.ok(serverService.add(insertView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ServerView> updateServer(@PathVariable Long id, @RequestBody ServerInsertView insertView) {
        return ResponseEntity.ok(serverService.save(id, insertView));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServer(@PathVariable Long id) {
        serverService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("{id}/files")
    public ResponseEntity<List<File>> getFileList(@PathVariable Long id, @RequestParam String path) {
        return ResponseEntity.ok(serverService.getFiles(id, path));
    }

    @GetMapping("/{id}/resources")
    public ResponseEntity<ResourceView> getServerResources(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getServerResource(id));
    }

    @GetMapping("/{id}/streams/start")
    public ResponseEntity<Boolean> startAllChannelsOnServer(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.startAllChannelsOnServer(id));
    }

    @GetMapping("/{id}/streams/stop")
    public ResponseEntity<Boolean> stopAllChannelsOnServer(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.stopAllChannelsOnServer(id));
    }

    @GetMapping("/{id}/streams/restart")
    public ResponseEntity<Boolean> restartAllChannelsOnServer(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.restartAllChannelsOnServer(id));
    }
}
