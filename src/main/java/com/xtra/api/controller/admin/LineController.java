package com.xtra.api.controller.admin;

import com.xtra.api.projection.line.LineInsertView;
import com.xtra.api.projection.line.LineView;
import com.xtra.api.service.admin.AdminLineServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lines")
public class LineController {
    private final AdminLineServiceImpl lineService;

    @Autowired
    public LineController(AdminLineServiceImpl lineService) {
        this.lineService = lineService;
    }

    @GetMapping("")
    public ResponseEntity<Page<LineView>> getLines(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                   @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(lineService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LineView> getLine(@PathVariable Long id) {
        return ResponseEntity.ok(lineService.getById(id));
    }

    @PostMapping("")
    public ResponseEntity<LineView> addLine(@RequestBody LineInsertView insertView) {
        return ResponseEntity.ok(lineService.add(insertView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LineView> updateLine(@PathVariable Long id, @RequestBody LineInsertView insertView) {
        return ResponseEntity.ok(lineService.save(id, insertView));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLine(@PathVariable Long id) {
        lineService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/block/{blocked}")
    public ResponseEntity<?> setLineBlocked(@PathVariable Long id, @PathVariable boolean blocked) {
        lineService.updateLineBlock(id, blocked);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/ban/{banned}")
    public ResponseEntity<?> setLineBanned(@PathVariable Long id, @PathVariable boolean banned) {
        lineService.updateLineBan(id, banned);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/kill_connections")
    public ResponseEntity<String> killLineConnections(@PathVariable Long id) {
        lineService.killAllConnections(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
