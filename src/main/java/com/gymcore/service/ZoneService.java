package com.gymcore.service;

import com.gymcore.dto.ZoneRequest;
import com.gymcore.dto.ZoneResponse;
import com.gymcore.entity.Location;
import com.gymcore.entity.Zone;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.LocationRepository;
import com.gymcore.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ZoneService {
    private final ZoneRepository zoneRepository;
    private final LocationRepository locationRepository;

    public ZoneResponse createZone(ZoneRequest request){
        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + request.getLocationId()));

        if (zoneRepository.existsByNameAndLocationId(request.getName(), request.getLocationId())){
            throw new DuplicateResourceException(
                    "Zone with name: " + request.getName() + "'already exists");
        }

        Zone zone = new Zone();
        zone.setName(request.getName());
        zone.setType(request.getType());
        zone.setAreaM2(request.getAreaM2());
        zone.setCapacity(request.getCapacity());
        zone.setLocation(location);

        Zone saved = zoneRepository.save(zone);

        return toResponse(saved);
    }

    private ZoneResponse toResponse(Zone zone) {
        return new ZoneResponse(
                zone.getId(),
                zone.getName(),
                zone.getType().name(),
                zone.getAreaM2(),
                zone.getCapacity(),
                zone.getLocation().getId(),
                zone.getCreatedAt()
        );
    }

    public List<ZoneResponse> getZonesByLocation(Long locationId){
        return zoneRepository.findByLocationId(locationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ZoneResponse getZoneById(Long id){
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Zone not found with id: " + id));
        return toResponse(zone);
    }

    public ZoneResponse updateZone(Long id, ZoneRequest request){
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Zone not found with id: " + id));

        zone.setName(request.getName());
        zone.setType(request.getType());
        zone.setAreaM2(request.getAreaM2());
        zone.setCapacity(request.getCapacity());

        return toResponse(zoneRepository.save(zone));
    }

    public void deleteZone(Long id){
        Zone zone = zoneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Zone not found with id: " + id));
        zoneRepository.delete(zone);
    }
}
