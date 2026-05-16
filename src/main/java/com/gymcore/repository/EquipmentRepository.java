package com.gymcore.repository;

import com.gymcore.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByZoneId(Long zoneId);
    Boolean existsByNameAndZoneId(String name, Long zoneId);
}
