package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.downloadlist.DownloadListInsertView;
import com.xtra.api.projection.admin.downloadlist.DownloadListView;
import com.xtra.api.service.admin.DownloadListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("download_lists")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class DownloadListController {
    private final DownloadListService dlService;

    @Autowired
    public DownloadListController(DownloadListService downloadListService) {
        this.dlService = downloadListService;
    }

    @PreAuthorize("hasAnyAuthority({'downloadList_manage'})")
    @GetMapping("")
    public ResponseEntity<Page<DownloadListView>> getDownloadLists(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                   @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(dlService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'downloadList_manage'})")
    @GetMapping("/{id}")
    public ResponseEntity<DownloadListView> getDownloadList(@PathVariable Long id) {
        return ResponseEntity.ok(dlService.getViewById(id));
    }

    @PreAuthorize("hasAnyAuthority({'downloadList_manage'})")
    @PostMapping("")
    public ResponseEntity<DownloadListView> addDownloadList(@RequestBody DownloadListInsertView downloadListView) {
        return ResponseEntity.ok(dlService.save(downloadListView));
    }

    @PreAuthorize("hasAnyAuthority({'downloadList_manage'})")
    @PatchMapping("/{id}")
    public ResponseEntity<DownloadListView> updateDownloadList(@PathVariable Long id, @RequestBody DownloadListInsertView downloadListView) {
        return ResponseEntity.ok(dlService.updateOrFail(id, downloadListView));
    }

    @PreAuthorize("hasAnyAuthority({'downloadList_manage'})")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDownloadList(@PathVariable Long id) {
        dlService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
