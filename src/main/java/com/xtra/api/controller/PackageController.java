package com.xtra.api.controller;

import com.xtra.api.model.Package;
import com.xtra.api.service.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/packages")
public class PackageController {
    private final PackageService packageService;

    @Autowired
    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("")
    public ResponseEntity<Page<Package>> getPackages(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(packageService.findAll(search, pageNo, pageSize, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Package> getPackage(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.findByIdOrFail(id));
    }

    @PostMapping("")
    public ResponseEntity<Package> addPackage(@RequestBody @Valid Package _package) {
        return ResponseEntity.ok(packageService.add(_package));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Package> updatePackage(@PathVariable Long id, @RequestBody @Valid Package _package) {
        return ResponseEntity.ok(packageService.updateOrFail(id, _package));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePackage(@PathVariable Long id) {
        packageService.deleteOrFail(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
