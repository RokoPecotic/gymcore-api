package com.gymcore.repository;

import com.gymcore.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
        Boolean existsByNameAndTenantId(String name, Long tenantId);
        List<Location> findByTenantId(Long tenantId);
}
