package com.gymcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EquipmentResponse {
    private Long id;
    private String name;
    private String brand;
    private Integer quantity;
    private String status;
    private LocalDate purchaseDate;
    private LocalDate lastMaintenance;
    private LocalDate nextMaintenance;
    private Long zoneId;
    private LocalDateTime createdAt;
}