package com.xtra.api.controller.reseller;

import com.xtra.api.projection.reseller.line.LineCreateView;
import com.xtra.api.projection.reseller.line.LineView;
import com.xtra.api.service.reseller.ResellerLineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("resellerLineController")
@RequestMapping("users/current/lines")
public class LineController {
    private final ResellerLineServiceImpl lineService;

    @Autowired
    public LineController(ResellerLineServiceImpl lineService) {
        this.lineService = lineService;
    }

    @GetMapping("")
    public ResponseEntity<Page<LineView>> getResellerLines(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                           @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(lineService.getAllLines(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineView> getResellerLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.getById(id));
    }

    @PostMapping("")
    public ResponseEntity<LineView> createResellerLine(@RequestBody LineCreateView createView) {
        return ResponseEntity.ok(lineService.createLine(createView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LineView> updateResellerLine(@PathVariable Long id, @RequestBody LineCreateView createView) {
        return ResponseEntity.ok(lineService.updateLine(id, createView));
    }

    @PatchMapping("/{id}/extend")
    public ResponseEntity<LineView> extendResellerLine(@PathVariable Long id, @RequestBody Long packageId) {
        return ResponseEntity.ok(lineService.extendLine(id, packageId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResellerLine(@PathVariable Long id) {
        lineService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("{id}/download")
    public ResponseEntity<String> downloadOwnLine(@PathVariable Long id) {
        return lineService.downloadLinePlaylist(id);
    }

    @PatchMapping("/{id}/block/{blocked}")
    public ResponseEntity<?> setResellerLineBlocked(@PathVariable Long id, @PathVariable boolean blocked) {
        lineService.updateLineBlock(id, blocked);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/ban/{banned}")
    public ResponseEntity<?> setResellerLineBanned(@PathVariable Long id, @PathVariable boolean banned) {
        lineService.updateLineBan(id, banned);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/kill_connections")
    public ResponseEntity<String> killResellerLineConnections(@PathVariable Long id) {
        lineService.killAllConnections(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
