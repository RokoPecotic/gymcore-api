package com.gymcore.service;

import com.gymcore.dto.LocationRequest;
import com.gymcore.dto.LocationResponse;
import com.gymcore.entity.Location;
import com.gymcore.entity.Tenant;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.LocationRepository;
import com.gymcore.repository.TenantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.swing.text.html.Option;
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
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private LocationService locationService;

    private Tenant tenant;
    private Location location;
    private LocationRequest request;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("GymCore");

        location = new Location();
        location.setId(1L);
        location.setName("GymCore Split Spinut");
        location.setAddress("Spinutska 10");
        location.setCity("Split");
        location.setTotalAreaM2(450);
        location.setCapacity(120);
        location.setEmail("spinut@gymcore.hr");
        location.setActive(true);
        location.setTenant(tenant);
        location.setCreatedAt(LocalDateTime.now());

        request = new LocationRequest();
        request.setName("GymCore Split Spinut");
        request.setAddress("Spinutska 10");
        request.setCity("Split");
        request.setTotalAreaM2(450);
        request.setCapacity(120);
        request.setEmail("spinut@gymcore.hr");
        request.setTenantId(1L);
    }

    @Test
    @DisplayName("Should create location when data is valid")
    void shouldCreateLocation_whenDataIsValid(){
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(locationRepository.existsByNameAndTenantId(anyString(), anyLong())).thenReturn(false);
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationResponse result = locationService.createLocation(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("GymCore Split Spinut");
        assertThat(result.getCapacity()).isEqualTo(120);
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    @DisplayName("Should throw exception when Tenant is not found")
    void shouldThrowException_whenTenantNotFound(){
        when(tenantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.createLocation(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tenant not found");

        verify(locationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when location name already exists")
    void shouldThrowException_whenLocationNameExists(){
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(locationRepository.existsByNameAndTenantId(anyString(), anyLong()))
                .thenReturn(true);

        assertThatThrownBy(() -> locationService.createLocation(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already exists");

        verify(locationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return location when id exists")
    void shouldReturnLocation_whenIdExists(){
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        LocationResponse result = locationService.getLocationById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("GymCore Split Spinut");
    }

    @Test
    @DisplayName("Should throw exception when location id not found")
    void shouldThrowException_whenLocationIdNotFound(){
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.getLocationById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found");
    }

    @Test
    @DisplayName("Should return all locations for tenant")
    void shouldReturnAllLocations_forTenant() {
        when(locationRepository.findByTenantId(1L)).thenReturn(List.of(location));

        List<LocationResponse> result = locationService.getLocationsByTenant(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("GymCore Split Spinut");
    }

    @Test
    @DisplayName("Should update location when id exists")
    void shouldUpdateLocation_whenIdExists() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(locationRepository.save(any(Location.class))).thenReturn(location);

        LocationResponse result = locationService.updateLocation(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("GymCore Split Spinut");
        verify(locationRepository).save(any(Location.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent location")
    void shouldThrowException_whenUpdatingNonExistentLocation() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.updateLocation(99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found");

        verify(locationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete location when id exists")
    void shouldDeleteLocation_whenIdExists() {
        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));

        locationService.deleteLocation(1L);

        verify(locationRepository).delete(location);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent location")
    void shouldThrowException_whenDeletingNonExistentLocation() {
        when(locationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> locationService.deleteLocation(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Location not found");

        verify(locationRepository, never()).delete(any());
    }

}