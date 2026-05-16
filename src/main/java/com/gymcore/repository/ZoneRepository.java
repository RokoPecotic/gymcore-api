package com.gymcore.repository;

import com.gymcore.entity.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    List<Zone> findByLocationId(Long locationId);
    boolean existsByNameAndLocationId(String name, Long locationId);
}
