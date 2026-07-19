package com.gymcore.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/location/{locationId}")
    public ResponseEntity<LocationAnalyticsResponse> getLocationAnalytics(
            @PathVariable Long locationId) {
        return ResponseEntity.ok(analyticsService.getLocationAnalytics(locationId));
    }

    @GetMapping("/franchise/{tenantId}")
    public ResponseEntity<FranchiseAnalyticsResponse> getFranchiseAnalytics(
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(analyticsService.getFranchiseAnalytics(tenantId));
    }

    @GetMapping("/capacity/{tenantId}")
    public ResponseEntity<List<CapacityInsightResponse>> getCapacityInsights(
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(analyticsService.getCapacityInsights(tenantId));
    }
}
