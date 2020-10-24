package com.xtra.api.controller;

import com.xtra.api.model.File;
import com.xtra.api.model.Resource;
import com.xtra.api.model.Server;
import com.xtra.api.service.ServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Page<Server>> getServers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(serverService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PostMapping("")
    public ResponseEntity<Server> addServer(@Valid @RequestBody Server server) {
        return ResponseEntity.ok(serverService.insert(server));
    }

    @GetMapping("{id}/files")
    public ResponseEntity<List<File>> getFileList(@PathVariable Long id, @RequestParam String path) {
        return ResponseEntity.ok(serverService.getFiles(id, path));
    }

    @GetMapping("{id}/resources")
    public ResponseEntity<Resource> getMemoryUsage(@PathVariable Long id, @RequestParam String interfaceName) {
        return ResponseEntity.ok(serverService.getResourceUsage(id, interfaceName));
    }

    @GetMapping("details")
    public ResponseEntity<List<Server>> details() {
        return ResponseEntity.ok(serverService.details());
    }

    @GetMapping("get-res/{id}")
    public ResponseEntity<Resource> getRes(@PathVariable Long id) {
        return ResponseEntity.ok(serverService.getRes(id));
    }

    @GetMapping("test-port")
    public int testPort(HttpServletRequest request){
        return request.getLocalPort();
    }

}
