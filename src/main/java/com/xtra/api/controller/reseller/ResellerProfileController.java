package com.xtra.api.controller.reseller;

import com.xtra.api.service.admin.ResellerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("reseller/profile")
@PreAuthorize("hasAnyRole({'RESELLER'})")
public class ResellerProfileController {
    private final ResellerService resellerService;

    public ResellerProfileController(ResellerService resellerService) {
        this.resellerService = resellerService;
    }

    @GetMapping("")
    public ResponseEntity<?> getProfile(){
        return ResponseEntity.ok(resellerService.getResellerProfile());
    }

}
