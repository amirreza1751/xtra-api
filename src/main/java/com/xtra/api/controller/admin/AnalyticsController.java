package com.xtra.api.controller.admin;

import com.xtra.api.projection.admin.analytics.AnalyticsData;
import com.xtra.api.service.admin.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("analytics")
public class AnalyticsController {
    final private AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("")
    public ResponseEntity<?> getAnalyticsData() {
        return ResponseEntity.ok(analyticsService.getData());
    }
}
