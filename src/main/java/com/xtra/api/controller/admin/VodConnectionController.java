package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.connection.BlockIpRequest;
import com.xtra.api.projection.admin.connection.ConnectionView;
import com.xtra.api.service.admin.ConnectionService;
import com.xtra.api.service.admin.VodConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vod-connections")
public class VodConnectionController {
    final private VodConnectionService vodConnectionService;

    @Autowired
    public VodConnectionController(VodConnectionService vodConnectionService) {

        this.vodConnectionService = vodConnectionService;
    }

    @GetMapping("")
    public ResponseEntity<Page<ConnectionView>> getActiveConnections(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                     @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(vodConnectionService.getActiveConnections(pageNo, pageSize, sortBy, sortDir));
    }

    @PostMapping("block-ip")
    public ResponseEntity<?> blockIpAddress(@RequestBody BlockIpRequest blockIpRequest) {
        vodConnectionService.blockIp(blockIpRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/clear")
    public ResponseEntity<?> deleteTeleRecord() {
        vodConnectionService.deleteOldConnections();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
