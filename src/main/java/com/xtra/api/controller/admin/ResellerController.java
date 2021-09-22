package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.user.UserSimpleView;
import com.xtra.api.projection.admin.user.reseller.*;
import com.xtra.api.service.admin.ResellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resellers")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class ResellerController {
    ResellerService resellerService;

    @Autowired
    public ResellerController(ResellerService resellerService) {
        this.resellerService = resellerService;
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @GetMapping("")
    public ResponseEntity<Page<ResellerListView>> getResellers(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(resellerService.getList(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @GetMapping("/list")
    public ResponseEntity<Page<UserSimpleView>> getResellersSimpleList(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                                                       @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(resellerService.getSimpleList(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @GetMapping("/{id}")
    public ResponseEntity<ResellerView> getReseller(@PathVariable Long id) {
        return ResponseEntity.ok(resellerService.getReseller(id));
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @PostMapping("")
    public ResponseEntity<ResellerView> addReseller(@RequestBody ResellerInsertView resellerInsertView) {
        return ResponseEntity.ok(resellerService.add(resellerInsertView));
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @PatchMapping("/{id}")
    public ResponseEntity<ResellerView> updateReseller(@PathVariable Long id, @RequestBody ResellerInsertView reseller) {
        return ResponseEntity.ok(resellerService.save(id, reseller));
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @PatchMapping("/batches")
    public ResponseEntity<?> updateResellers(@RequestBody ResellerBatchView resellersView) {
        resellerService.saveAll(resellersView);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @PatchMapping("/{id}/credits")
    public ResponseEntity<?> updateResellerCredits(@PathVariable Long id, @RequestBody ResellerCreditChangeView creditChangeView) {
        resellerService.updateCredits(id, creditChangeView);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReseller(@PathVariable Long id, @RequestParam("new_owner_id") Long newOwnerId) {
        resellerService.deleteReseller(id, newOwnerId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize("hasAnyAuthority({'resellers_manage'})")
    @DeleteMapping("/batches")
    public ResponseEntity<?> updateResellers(@RequestBody ResellerBatchDeleteView resellersView) {
        resellerService.deleteAll(resellersView);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signUpReseller(@RequestBody ResellerSignUpView resellerSignUpView) {
        resellerService.signUp(resellerSignUpView);
        return ResponseEntity.ok().build();
    }
}
