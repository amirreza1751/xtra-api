package com.xtra.api.controller.admin;

import com.xtra.api.model.Connection;
import com.xtra.api.model.ConnectionId;
import com.xtra.api.projection.admin.ConnectionIdView;
import com.xtra.api.projection.admin.ConnectionView;
import com.xtra.api.service.admin.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PostMapping("/kill")
    public ResponseEntity<?> killConnection(ConnectionIdView connectionIdView) {
        connectionService.endConnection(connectionIdView);
        return ResponseEntity.ok().build();
    }

    @Autowired
    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/batch")
    public ResponseEntity<?> batchCreateOrUpdateConnections(@RequestBody List<Connection> connections, @RequestParam String portNumber, HttpServletRequest request) {
        connectionService.batchCreateOrUpdate(connections, portNumber, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<?> batchDeleteConnection(@RequestBody List<Connection> connections) {
        connectionService.batchDelete(connections);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteLineConnection(@RequestBody ConnectionId activityId) {
        connectionService.deleteConnection(activityId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
