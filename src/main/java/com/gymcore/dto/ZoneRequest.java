package com.gymcore.dto;

import com.gymcore.entity.ZoneType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZoneRequest {
    @NotBlank
    private String name;

    @NotNull
    private ZoneType type;

    @NotNull
    private Integer areaM2;

    @NotNull
    private Integer capacity;

    @NotNull
    private Long locationId;
}
