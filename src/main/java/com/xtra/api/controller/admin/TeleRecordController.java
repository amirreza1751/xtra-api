package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.catchup.TeleRecordListView;
import com.xtra.api.service.admin.TeleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
@RestController
public class TeleRecordController {

    private final TeleRecordService teleRecordService;

    @Autowired
    public TeleRecordController(TeleRecordService teleRecordService) {
        this.teleRecordService = teleRecordService;
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @GetMapping("/channels/{id}/tele-records")
    public ResponseEntity<Page<TeleRecordListView>> getAll(@PathVariable Long id,
                                                           @RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                           @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(teleRecordService.findAllByChannel(id, pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'movies_manage'})")
    @DeleteMapping("/tele-records/{id}")
    public ResponseEntity<?> deleteTeleRecord(@PathVariable Long id) {
        teleRecordService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
