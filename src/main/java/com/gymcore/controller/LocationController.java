package com.gymcore.controller;

import com.gymcore.dto.LocationRequest;
import com.gymcore.dto.LocationResponse;
import com.gymcore.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody LocationRequest request){
        return ResponseEntity.ok(locationService.createLocation(request));
    }

    @GetMapping("/tenant/{tenantId}")
    public ResponseEntity<List<LocationResponse>> getLocationsByTenant(
            @PathVariable Long tenantId) {
        return ResponseEntity.ok(locationService.getLocationsByTenant(tenantId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(
            @PathVariable Long id){
        return ResponseEntity.ok(locationService.getLocationById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Long id, @Valid @RequestBody LocationRequest request){
        return ResponseEntity.ok(locationService.updateLocation(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable Long id){
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
