package com.xtra.api.controller.admin;

import com.xtra.api.service.admin.BackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/backup")
public class BackupController {
    private final BackupService backupService;

    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    @GetMapping("/perform")
    public ResponseEntity<?> createManualBackup() {
        backupService.createBackup(true, Collections.emptyList());
        return ResponseEntity.ok().build();
    }
}
