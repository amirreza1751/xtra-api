package com.xtra.api.controller.reseller;

import com.xtra.api.projection.admin.user.reseller.ResellerInsertView;
import com.xtra.api.projection.admin.user.reseller.ResellerView;
import com.xtra.api.service.reseller.SubresellerService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/current/sub-resellers")
public class SubresellerController {
    private final SubresellerService subresellerService;

    public SubresellerController(SubresellerService subresellerService) {
        this.subresellerService = subresellerService;
    }

    @GetMapping("")
    public ResponseEntity<Page<ResellerView>> getSubresellers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                              @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(subresellerService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResellerView> getSubreseller(@PathVariable Long id) {
        return ResponseEntity.ok(subresellerService.getReseller(id));
    }

    @PostMapping("")
    public ResponseEntity<ResellerView> addSubreseller(@RequestBody ResellerInsertView insertView) {
        return ResponseEntity.ok(subresellerService.addSubreseller(insertView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResellerView> updateSubreseller(@PathVariable Long id, @RequestBody ResellerInsertView insertView) {
        return ResponseEntity.ok(subresellerService.updateSubreseller(id, insertView));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSubreseller(@PathVariable Long id) {
        subresellerService.deleteSubreseller(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{id}/lines/enable")
    public ResponseEntity<?> enableAllResellerLines(@PathVariable Long id) {
        subresellerService.enableResellerLines(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/{id}/lines/disable")
    public ResponseEntity<?> disableAllResellerLines(@PathVariable Long id) {
        subresellerService.disableResellerLines(id);
        return ResponseEntity.ok().build();
    }
}
