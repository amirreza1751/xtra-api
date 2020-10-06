package com.xtra.api.controller;

import com.xtra.api.model.DownloadList;
import com.xtra.api.model.Line;
import com.xtra.api.service.DownloadListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("download_lists")
public class DownloadListController {
    private final DownloadListService downloadListService;

    @Autowired
    public DownloadListController(DownloadListService downloadListService) {
        this.downloadListService = downloadListService;
    }

    @GetMapping("")
    public ResponseEntity<Page<DownloadList>> getDownloadLists(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(downloadListService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/default")
    public DownloadList getSystemDefaultDownloadList(){
        return downloadListService.getDefaultDownloadList();
    }

    @GetMapping("")
    public List<DownloadList> getDownloadListsByUserId(Long userId){
        return downloadListService.getDownloadListsByUserId(userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DownloadList> getDownloadList(@PathVariable Long id) {
        return ResponseEntity.ok(downloadListService.findByIdOrFail(id));
    }

    @PostMapping("")
    public ResponseEntity<DownloadList> addDownloadList(@RequestBody @Valid DownloadList downloadList) {
        return ResponseEntity.ok(downloadListService.add(downloadList));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DownloadList> updateDownloadList(@PathVariable Long id, @RequestBody @Valid DownloadList downloadList) {
        return ResponseEntity.ok(downloadListService.updateOrFail(id, downloadList));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDownloadList(@PathVariable Long id) {
        downloadListService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
