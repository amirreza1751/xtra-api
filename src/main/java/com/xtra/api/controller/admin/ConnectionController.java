package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.connection.BlockIpRequest;
import com.xtra.api.projection.admin.connection.ConnectionView;
import com.xtra.api.service.admin.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/connections")
public class ConnectionController {
    final private ConnectionService connectionService;

    @Autowired
    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @GetMapping("")
    public ResponseEntity<Page<ConnectionView>> getActiveConnections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                     @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(connectionService.getActiveConnections(pageNo, pageSize, sortBy, sortDir));
    }

    @PostMapping("block-ip")
    public ResponseEntity<?> blockIpAddress(@RequestBody BlockIpRequest blockIpRequest) {
        connectionService.blockIp(blockIpRequest);
        return ResponseEntity.ok().build();
    }
}
