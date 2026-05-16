package com.gymcore.controller;

import com.gymcore.dto.ZoneRequest;
import com.gymcore.dto.ZoneResponse;
import com.gymcore.service.ZoneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/zones")
@RequiredArgsConstructor
public class ZoneController {
    private final ZoneService zoneService;

    @PostMapping
    public ResponseEntity<ZoneResponse> createZone(
            @Valid @RequestBody ZoneRequest request){
        return ResponseEntity.ok(zoneService.createZone(request));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<ZoneResponse>> getZonesByLocation(
            @PathVariable Long locationId) {
        return ResponseEntity.ok(zoneService.getZonesByLocation(locationId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ZoneResponse> getZoneById(
            @PathVariable Long id){
        return ResponseEntity.ok(zoneService.getZoneById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ZoneResponse> updateZone(
            @PathVariable Long id, @Valid @RequestBody ZoneRequest request){
        return ResponseEntity.ok(zoneService.updateZone(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteZone(
            @PathVariable Long id){
        zoneService.deleteZone(id);
        return ResponseEntity.noContent().build();
    }
}
