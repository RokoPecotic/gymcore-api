package com.gymcore.service;

import com.gymcore.dto.ZoneRequest;
import com.gymcore.dto.ZoneResponse;
import com.gymcore.entity.Location;
import com.gymcore.entity.Zone;
import com.gymcore.entity.ZoneType;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.LocationRepository;
import com.gymcore.repository.ZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
class ZoneServiceTest {

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private ZoneService zoneService;

    private Location location;
    private Zone zone;
    private ZoneRequest request;

    @BeforeEach
    void setUp() {
        location = new Location();
        location.setId(1L);
        location.setName("GymCore Split Spinut");

        zone = new Zone();
        zone.setId(1L);
        zone.setName("Cardio Zone");
        zone.setType(ZoneType.CARDIO);
        zone.setAreaM2(100);
        zone.setCapacity(20);
        zone.setLocation(location);
        zone.setCreatedAt(LocalDateTime.now());

        request = new ZoneRequest();
        request.setName("Cardio Zone");
        request.setType(ZoneType.CARDIO);
        request.setAreaM2(100);
        request.setCapacity(20);
        request.setLocationId(1L);
    }

    @Test
    @DisplayName("Should create zone when data is valid")
    void shouldCreateZone_whenDataIsValid() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(zoneRepository.existsByNameAndLocationId(anyString(), anyLong()))
                .thenReturn(false);
        when(zoneRepository.save(any(Zone.class))).thenReturn(zone);

        ZoneResponse result = zoneService.createZone(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Cardio Zone");
        assertThat(result.getType()).isEqualTo("CARDIO");
        assertThat(result.getCapacity()).isEqualTo(20);
        verify(zoneRepository).save(any(Zone.class));
    }

    @Test
    @DisplayName("Should throw exception when location not found")
    void shouldThrowException_whenLocationNotFound() {
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.createZone(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found");

        verify(zoneRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when zone name already exists")
    void shouldThrowException_whenZoneNameExists() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(zoneRepository.existsByNameAndLocationId(anyString(), anyLong()))
                .thenReturn(true);

        assertThatThrownBy(() -> zoneService.createZone(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(zoneRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return zone when id exists")
    void shouldReturnZone_whenIdExists() {
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        ZoneResponse result = zoneService.getZoneById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Cardio Zone");
    }

    @Test
    @DisplayName("Should throw exception when zone id not found")
    void shouldThrowException_whenZoneIdNotFound() {
        when(zoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.getZoneById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Zone not found");
    }

    @Test
    @DisplayName("Should return all zones for location")
    void shouldReturnAllZones_forLocation() {
        when(zoneRepository.findByLocationId(1L)).thenReturn(List.of(zone));

        List<ZoneResponse> result = zoneService.getZonesByLocation(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Cardio Zone");
    }

    @Test
    @DisplayName("Should update zone when id exists")
    void shouldUpdateZone_whenIdExists() {
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));
        when(zoneRepository.save(any(Zone.class))).thenReturn(zone);

        ZoneResponse result = zoneService.updateZone(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Cardio Zone");
        verify(zoneRepository).save(any(Zone.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent zone")
    void shouldThrowException_whenUpdatingNonExistentZone() {
        when(zoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.updateZone(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Zone not found");

        verify(zoneRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete zone when id exists")
    void shouldDeleteZone_whenIdExists() {
        when(zoneRepository.findById(1L)).thenReturn(Optional.of(zone));

        zoneService.deleteZone(1L);

        verify(zoneRepository).delete(zone);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent zone")
    void shouldThrowException_whenDeletingNonExistentZone() {
        when(zoneRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> zoneService.deleteZone(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Zone not found");

        verify(zoneRepository, never()).delete(any());
    }
}