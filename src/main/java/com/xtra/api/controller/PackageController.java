package com.xtra.api.controller;

import com.xtra.api.projection.package_.PackageInsertView;
import com.xtra.api.projection.package_.PackageView;
import com.xtra.api.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/packages")
public class PackageController {
    private final PackageService packageService;

    @Autowired
    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("")
    public ResponseEntity<Page<PackageView>> getPackages(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(packageService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackageView> getPackage(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.getViewById(id));
    }

    @PostMapping("")
    public ResponseEntity<PackageView> addPackage(@RequestBody PackageInsertView packageView) {
        return ResponseEntity.ok(packageService.add(packageView));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PackageView> updatePackage(@PathVariable Long id, @RequestBody PackageInsertView packageView) {
        return ResponseEntity.ok(packageService.save(id, packageView));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable Long id) {
        packageService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
