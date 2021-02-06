package com.xtra.api.controller.reseller;

import com.xtra.api.projection.admin.downloadlist.DownloadListInsertView;
import com.xtra.api.projection.admin.downloadlist.DownloadListView;
import com.xtra.api.service.reseller.DownloadListServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("resellerDownloadListController")
@RequestMapping("/users/current/download-lists")
public class DownloadListController {
    private final DownloadListServiceImpl dlService;

    public DownloadListController(DownloadListServiceImpl dlService) {
        this.dlService = dlService;
    }

    @GetMapping("")
    public ResponseEntity<List<DownloadListView>> getResellerDownloadLists() {
        return ResponseEntity.ok(dlService.getResellerDownloadLists());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DownloadListView> getDownloadList(@PathVariable Long id) {
        return ResponseEntity.ok(dlService.getViewById(id));
    }

    @PostMapping("")
    public ResponseEntity<DownloadListView> addDownloadList(@RequestBody DownloadListInsertView downloadListView) {
        return ResponseEntity.ok(dlService.save(downloadListView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DownloadListView> updateDownloadList(@PathVariable Long id, @RequestBody DownloadListInsertView downloadListView) {
        return ResponseEntity.ok(dlService.updateOrFail(id, downloadListView));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDownloadList(@PathVariable Long id) {
        dlService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
