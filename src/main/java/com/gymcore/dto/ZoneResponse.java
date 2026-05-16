package com.gymcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ZoneResponse {
    private Long id;
    private String name;
    private String type;
    private Integer areaM2;
    private Integer capacity;
    private Long locationId;
    private LocalDateTime createdAt;
}
