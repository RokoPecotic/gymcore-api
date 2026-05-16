package com.gymcore.service;

import com.gymcore.dto.*;
import com.gymcore.entity.Equipment;
import com.gymcore.entity.Zone;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.EquipmentRepository;
import com.gymcore.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {
    private final ZoneRepository zoneRepository;
    private final EquipmentRepository equipmentRepository;

    public EquipmentResponse createEquipment(EquipmentRequest request) {

        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Zone not found with id: " + request.getZoneId()));

        if (equipmentRepository.existsByNameAndZoneId(request.getName(), request.getZoneId())) {
            throw new DuplicateResourceException(
                    "Zone with name '" + request.getName() + "' already exists");
        }

        Equipment equipment = new Equipment();
        equipment.setName(request.getName());
        equipment.setBrand(request.getBrand());
        equipment.setQuantity(request.getQuantity());
        equipment.setStatus(request.getStatus());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setLastMaintenance(request.getLastMaintenance());
        equipment.setNextMaintenance(request.getNextMaintenance());
        equipment.setZone(zone);

        Equipment saved = equipmentRepository.save(equipment);

        return toResponse(saved);
    }

    private EquipmentResponse toResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getBrand(),
                equipment.getQuantity(),
                equipment.getStatus().name(),
                equipment.getPurchaseDate(),
                equipment.getLastMaintenance(),
                equipment.getNextMaintenance(),
                equipment.getZone().getId(),
                equipment.getCreatedAt()
        );
    }

    public List<EquipmentResponse> getEquipmentsByZone(Long zoneId){
        return equipmentRepository.findByZoneId(zoneId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public EquipmentResponse getEquipmentById(Long id){
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + id));
        return toResponse(equipment);
    }

    public EquipmentResponse updateEquipment(Long id, EquipmentRequest request){
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + id));

        equipment.setName(request.getName());
        equipment.setBrand(request.getBrand());
        equipment.setQuantity(request.getQuantity());
        equipment.setStatus(request.getStatus());
        equipment.setPurchaseDate(request.getPurchaseDate());
        equipment.setLastMaintenance(request.getLastMaintenance());
        equipment.setNextMaintenance(request.getNextMaintenance());

        return toResponse(equipmentRepository.save(equipment));
    }

    public void deleteEquipment(Long id){
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Equipment not found with id: " + id));
        equipmentRepository.delete(equipment);
    }
}
