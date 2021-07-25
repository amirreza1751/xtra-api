package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.catchup.TeleRecordListView;
import com.xtra.api.projection.admin.connection.BlockIpRequest;
import com.xtra.api.projection.admin.connection.ConnectionView;
import com.xtra.api.service.admin.ConnectionService;
import com.xtra.api.service.admin.TeleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TeleRecordController {

    private final TeleRecordService teleRecordService;

    @Autowired
    public TeleRecordController(ConnectionService connectionService, TeleRecordService teleRecordService) {

        this.teleRecordService = teleRecordService;
    }

    @GetMapping("/channels/{id}/tele-records")
    public ResponseEntity<Page<TeleRecordListView>> getAll(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize
            , @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(teleRecordService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @DeleteMapping("/tele-records/{id}")
    public ResponseEntity<?> deleteTeleRecord(@PathVariable Long id) {
        teleRecordService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
