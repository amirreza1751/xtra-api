package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.SettingView;
import com.xtra.api.service.admin.SettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Map<String, String>> getSettingValues(@RequestBody List<String> settingKeys) {
        return ResponseEntity.ok(settingService.getSettings(settingKeys));
    }

    @PreAuthorize("hasAnyAuthority({'setting_manage'})")
    @PatchMapping("")
    public ResponseEntity<Void> updateSettingValues(@RequestBody List<SettingView> settings) {
        settingService.updateSettingValues(settings);
        return ResponseEntity.ok().build();
    }
}
