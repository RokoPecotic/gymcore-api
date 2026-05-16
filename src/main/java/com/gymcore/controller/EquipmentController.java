package com.gymcore.controller;

import com.gymcore.dto.EquipmentRequest;
import com.gymcore.dto.EquipmentResponse;
import com.gymcore.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {
    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<EquipmentResponse> createEquipment(
            @Valid @RequestBody EquipmentRequest request){
        return ResponseEntity.ok(equipmentService.createEquipment(request));
    }

    @GetMapping("/zone/{zoneId}")
    public ResponseEntity<List<EquipmentResponse>> getEquipmentsByZone(
            @PathVariable Long zoneId) {
        return ResponseEntity.ok(equipmentService.getEquipmentsByZone(zoneId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> getEquipmentById(
            @PathVariable Long id){
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponse> updateEquipment(
            @PathVariable Long id, @Valid @RequestBody EquipmentRequest request){
        return ResponseEntity.ok(equipmentService.updateEquipment(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(
            @PathVariable Long id){
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}
