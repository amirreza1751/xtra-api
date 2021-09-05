package com.xtra.api.controller.line;

import com.xtra.api.projection.PasswordUpdateView;
import com.xtra.api.projection.admin.downloadlist.DownloadListView;
import com.xtra.api.projection.line.LineSecurityView;
import com.xtra.api.projection.line.line.LineSecurityUpdateView;
import com.xtra.api.projection.line.line.LineView;
import com.xtra.api.service.line.LineLineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("line/profile")
@PreAuthorize("hasAnyRole({'LINE'})")
public class LineProfileController {
    private final LineLineServiceImpl lineService;

    @Autowired
    public LineProfileController(LineLineServiceImpl lineService) {
        this.lineService = lineService;
    }

    @GetMapping("")
    public ResponseEntity<LineView> getLineProfile() {
        return ResponseEntity.ok(lineService.getProfile());
    }

    @GetMapping("download-list")
    public ResponseEntity<DownloadListView> getDownloadList() {
        return ResponseEntity.ok(lineService.getDownloadList());
    }

    @PatchMapping("download-list")
    public ResponseEntity<List<Long>> updateDownloadList(@RequestBody List<Long> collections) {
        return ResponseEntity.ok(lineService.updateDownloadList(collections));
    }

    @GetMapping("security")
    public ResponseEntity<LineSecurityView> getSecurityDetails() {
        return ResponseEntity.ok(lineService.getSecurityDetails());
    }

    @PatchMapping("security")
    public ResponseEntity<LineView> updateProfileSecurity(@RequestBody LineSecurityUpdateView updateView) {
        return ResponseEntity.ok(lineService.updateProfileSecurity(updateView));
    }

    @PatchMapping("security/password")
    public ResponseEntity<?> updateProfilePassword(@RequestBody PasswordUpdateView passwordUpdateView) {
        lineService.updateProfilePassword(passwordUpdateView);
        return ResponseEntity.ok().build();
    }
}
