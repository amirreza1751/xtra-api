package com.xtra.api.controller.admin;

import com.xtra.api.model.Connection;
import com.xtra.api.projection.admin.ConnectionView;
import com.xtra.api.service.admin.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/connections")
public class ConnectionController {
    final private ConnectionService connectionService;

    @GetMapping("")
    public ResponseEntity<Page<ConnectionView>> getActiveConnections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                     @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(connectionService.getActiveConnections(pageNo, pageSize, sortBy, sortDir));
    }

    @PostMapping("{id}/kill")
    public ResponseEntity<?> killConnection(@PathVariable("id") Long id) {
        connectionService.endConnection(id);
        return ResponseEntity.ok().build();
    }

    @Autowired
    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }


}
