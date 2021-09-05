package com.xtra.api.controller.admin;

import com.xtra.api.service.admin.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/backups")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class BackupController {
    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping("")
    public ResponseEntity<?> getBackupList(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                           @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(backupService.getBackupList(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'backup_manage'})")
    @GetMapping("/perform")
    public ResponseEntity<?> createManualBackup() {
        backupService.createBackup(true, Collections.emptyList());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority({'backup_manage'})")
    @GetMapping("{id}/restore")
    public ResponseEntity<?> restoreBackup(@PathVariable Long id) {
        backupService.restoreBackup(id);
        return ResponseEntity.ok().build();
    }
}
