package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.SettingView;
import com.xtra.api.service.admin.SettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("settings")
@PreAuthorize("hasAnyRole({'ADMIN', 'SUPER_ADMIN'})")
public class SettingController {
    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @PreAuthorize("hasAnyAuthority({'setting_manage'})")
    @GetMapping("")
    public ResponseEntity<List<SettingView>> getAllSettingValues() {
        return ResponseEntity.ok(settingService.getSettings());
    }

    @PreAuthorize("hasAnyAuthority({'setting_manage'})")
    @GetMapping("/{setting_key}")
    public ResponseEntity<SettingView> getSettingValue(@PathVariable(name = "setting_key") String settingKey) {
        return ResponseEntity.ok(settingService.getSetting(settingKey));
    }

    @PreAuthorize("hasAnyAuthority({'setting_manage'})")
    @PatchMapping("")
    public ResponseEntity<Void> updateSettingValues(@RequestBody List<SettingView> settings) {
        settingService.updateSettingValues(settings);
        return ResponseEntity.ok().build();
    }
}
