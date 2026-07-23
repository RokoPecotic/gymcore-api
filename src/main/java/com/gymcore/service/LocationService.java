package com.gymcore.service;

import com.gymcore.dto.LocationRequest;
import com.gymcore.dto.LocationResponse;
import com.gymcore.entity.Location;
import com.gymcore.entity.Tenant;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.gymcore.repository.TenantRepository;
import com.gymcore.repository.LocationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final TenantRepository tenantRepository;
    private final LocationRepository locationRepository;

    public LocationResponse createLocation(LocationRequest request) {

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tenant not found with id: " + request.getTenantId()));

        if (locationRepository.existsByNameAndTenantId(request.getName(), request.getTenantId())) {
            throw new DuplicateResourceException(
                    "Location with name '" + request.getName() + "' already exists");
        }

        Location location = new Location();
        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setCity(request.getCity());
        location.setTotalAreaM2(request.getTotalAreaM2());
        location.setCapacity(request.getCapacity());
        location.setEmail(request.getEmail());
        location.setTenant(tenant);

        Location saved = locationRepository.save(location);

        return toResponse(saved);
    }

    private LocationResponse toResponse(Location location) {
        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCity(),
                location.getTotalAreaM2(),
                location.getCapacity(),
                location.getEmail(),
                location.getActive(),
                location.getTenant().getId(),
                location.getCreatedAt()
        );
    }

    public List<LocationResponse> getLocationsByTenant(Long tenantId) {
        return locationRepository.findByTenantId(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public LocationResponse getLocationById(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id));
        return toResponse(location);
    }

    public LocationResponse updateLocation(Long id, LocationRequest request) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id));

        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setCity(request.getCity());
        location.setTotalAreaM2(request.getTotalAreaM2());
        location.setCapacity(request.getCapacity());
        location.setEmail(request.getEmail());

        return toResponse(locationRepository.save(location));
    }

    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + id));
        locationRepository.delete(location);
    }
}
