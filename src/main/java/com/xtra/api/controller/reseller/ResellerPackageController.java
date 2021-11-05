package com.xtra.api.controller.reseller;

import com.xtra.api.projection.admin.package_.PackageView;
import com.xtra.api.service.admin.PackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reseller/packages")
@PreAuthorize("hasAnyRole({'RESELLER'})")
public class ResellerPackageController {
    private final PackageService packageService;

    @Autowired
    public ResellerPackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    @GetMapping("")
    public ResponseEntity<Page<PackageView>> getPackages(@RequestParam(defaultValue = "0") int pageNo, @RequestParam(defaultValue = "25") int pageSize,
                                               @RequestParam(required = false) String search, @RequestParam(required = false) String sortBy, @RequestParam(required = false) String sortDir) {
        return ResponseEntity.ok(packageService.getAll(search, pageNo, pageSize, sortBy, sortDir));
    }
}
