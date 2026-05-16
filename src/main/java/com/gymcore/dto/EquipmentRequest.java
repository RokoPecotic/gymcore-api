package com.gymcore.dto;

import com.gymcore.entity.EquipmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EquipmentRequest {

    @NotBlank
    private String name;

    private String brand;

    @NotNull
    private Integer quantity;

    @NotNull
    private EquipmentStatus status;

    @NotNull
    private LocalDate purchaseDate;

    private LocalDate lastMaintenance;

    private LocalDate nextMaintenance;

    @NotNull
    private Long zoneId;
}