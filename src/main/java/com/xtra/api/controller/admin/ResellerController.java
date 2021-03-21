package com.xtra.api.controller.admin;

import com.xtra.api.mapper.admin.ResellerMapper;
import com.xtra.api.projection.admin.user.UserSimpleView;
import com.xtra.api.projection.admin.user.reseller.ResellerCreditChangeView;
import com.xtra.api.projection.admin.user.reseller.ResellerInsertView;
import com.xtra.api.projection.admin.user.reseller.ResellerView;
import com.xtra.api.service.admin.ResellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resellers")
public class ResellerController {
    ResellerService resellerService;
    ResellerMapper resellerMapper;

    @Autowired
    public ResellerController(ResellerService resellerService, ResellerMapper resellerMapper) {
        this.resellerService = resellerService;
        this.resellerMapper = resellerMapper;
    }

    @GetMapping("")
    public ResponseEntity<Page<ResellerView>> getResellers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                           @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(resellerService.findAll(search, pageNo, pageSize, sortBy, sortDir).map(resellerMapper::convertToView));
    }

    @GetMapping("/list")
    public ResponseEntity<Page<UserSimpleView>> getResellersSimpleList(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                       @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(resellerService.findAll(search, pageNo, pageSize, sortBy, sortDir).map(resellerMapper::convertToSimpleView));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResellerView> getReseller(@PathVariable Long id) {
        return ResponseEntity.ok(resellerMapper.convertToView(resellerService.findByIdOrFail(id)));
    }

    @PostMapping("")
    public ResponseEntity<ResellerView> addReseller(@RequestBody ResellerInsertView resellerInsertView) {
        return ResponseEntity.ok(resellerService.add(resellerInsertView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResellerView> updateReseller(@PathVariable Long id, @RequestBody ResellerInsertView reseller) {
        return ResponseEntity.ok(resellerService.save(id, reseller));
    }

    @PatchMapping("/{id}/credits")
    public ResponseEntity<?> updateResellerCredits(@PathVariable Long id, @RequestBody ResellerCreditChangeView creditChangeView) {
        resellerService.updateCredits(id, creditChangeView);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReseller(@PathVariable Long id) {
        resellerService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
