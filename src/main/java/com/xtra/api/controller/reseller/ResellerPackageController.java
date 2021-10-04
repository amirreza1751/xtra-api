package com.xtra.api.controller.reseller;

import com.xtra.api.projection.admin.package_.PackageInsertView;
import com.xtra.api.projection.admin.package_.PackageView;
import com.xtra.api.service.admin.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packages")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class ResellerPackageController {
    private final PackageService packageService;

    @Autowired
    public ResellerPackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @PreAuthorize("hasAnyAuthority({'package_manage'})")
    @GetMapping("")
    public ResponseEntity<Page<PackageView>> getPackages(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(packageService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @PreAuthorize("hasAnyAuthority({'package_manage'})")
    @GetMapping("/{id}")
    public ResponseEntity<PackageView> getPackage(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.getViewById(id));
    }

    @PreAuthorize("hasAnyAuthority({'package_manage'})")
    @PostMapping("")
    public ResponseEntity<PackageView> addPackage(@RequestBody PackageInsertView packageView) {
        return ResponseEntity.ok(packageService.add(packageView));
    }

    @PreAuthorize("hasAnyAuthority({'package_manage'})")
    @PatchMapping("/{id}")
    public ResponseEntity<PackageView> updatePackage(@PathVariable Long id, @RequestBody PackageInsertView packageView) {
        return ResponseEntity.ok(packageService.save(id, packageView));
    }

    @PreAuthorize("hasAnyAuthority({'package_manage'})")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable Long id) {
        packageService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
