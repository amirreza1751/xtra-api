package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.SettingView;
import com.xtra.api.service.admin.SettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("settings")
public class SettingController {
    private final SettingService settingService;

    public SettingController(SettingService settingService) {
        this.settingService = settingService;
    }

    @GetMapping("")
    public ResponseEntity<List<SettingView>> getSettingValues(@RequestBody List<String> settingKeys) {
        return ResponseEntity.ok(settingService.getSettings(settingKeys));
    }

    @PatchMapping("")
    public ResponseEntity<Void> updateSettingValues(@RequestBody List<SettingView> settings) {
        settingService.updateSettingValues(settings);
        return ResponseEntity.ok().build();
    }
}
