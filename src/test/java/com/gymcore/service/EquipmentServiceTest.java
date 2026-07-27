package com.gymcore.service;

import com.gymcore.dto.EquipmentRequest;
import com.gymcore.dto.EquipmentResponse;
import com.gymcore.entity.Equipment;
import com.gymcore.entity.EquipmentStatus;
import com.gymcore.entity.Zone;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.EquipmentRepository;
import com.gymcore.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    private Zone zone;
    private Equipment equipment;
    private EquipmentRequest request;

    @BeforeEach
    void setUp() {
        zone = new Zone();
        zone.setId(1L);
        zone.setName("Cardio Zone");

        equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("Treadmill");
        equipment.setBrand("Technogym");
        equipment.setQuantity(5);
        equipment.setStatus(EquipmentStatus.OPERATIONAL);
        equipment.setPurchaseDate(LocalDate.of(2024, 1, 15));
        equipment.setZone(zone);
        equipment.setCreatedAt(LocalDateTime.now());

        request = new EquipmentRequest();
        request.setName("Treadmill");
        request.setBrand("Technogym");
        request.setQuantity(5);
        request.setStatus(EquipmentStatus.OPERATIONAL);
        request.setPurchaseDate(LocalDate.of(2024, 1, 15));
        request.setZoneId(1L);
    }

    @Test
    @DisplayName("Should create equipment when data is valid")
    void shouldCreateEquipment_whenDataIsValid() {
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(equipmentRepository.existsByNameAndZoneId(anyString(), anyLong()))
                .thenReturn(false);
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

        EquipmentResponse result = equipmentService.createEquipment(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Treadmill");
        assertThat(result.getBrand()).isEqualTo("Technogym");
        assertThat(result.getQuantity()).isEqualTo(5);
        assertThat(result.getStatus()).isEqualTo("OPERATIONAL");
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Should throw exception when zone not found")
    void shouldThrowException_whenZoneNotFound() {
        when(zoneRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipmentService.createEquipment(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Zone not found");

        verify(equipmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when equipment name already exists")
    void shouldThrowException_whenEquipmentNameExists() {
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(equipmentRepository.existsByNameAndZoneId(anyString(), anyLong()))
                .thenReturn(true);

        assertThatThrownBy(() -> equipmentService.createEquipment(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(equipmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return equipment when id exists")
    void shouldReturnEquipment_whenIdExists() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        EquipmentResponse result = equipmentService.getEquipmentById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Treadmill");
    }

    @Test
    @DisplayName("Should throw exception when equipment id not found")
    void shouldThrowException_whenEquipmentIdNotFound() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipmentService.getEquipmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Equipment not found");
    }

    @Test
    @DisplayName("Should return all equipment for zone")
    void shouldReturnAllEquipment_forZone() {
        when(equipmentRepository.findByZoneId(1L)).thenReturn(List.of(equipment));

        List<EquipmentResponse> result = equipmentService.getEquipmentsByZone(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Treadmill");
    }

    @Test
    @DisplayName("Should update equipment when id exists")
    void shouldUpdateEquipment_whenIdExists() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

        EquipmentResponse result = equipmentService.updateEquipment(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Treadmill");
        verify(equipmentRepository).save(any(Equipment.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent equipment")
    void shouldThrowException_whenUpdatingNonExistentEquipment() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipmentService.updateEquipment(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Equipment not found");

        verify(equipmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete equipment when id exists")
    void shouldDeleteEquipment_whenIdExists() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        equipmentService.deleteEquipment(1L);

        verify(equipmentRepository).delete(equipment);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent equipment")
    void shouldThrowException_whenDeletingNonExistentEquipment() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipmentService.deleteEquipment(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Equipment not found");

        verify(equipmentRepository, never()).delete(any());
    }
}