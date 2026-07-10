package com.gymcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OccupancyResponse {
    private Long locationId;
    private String locationName;
    private Integer currentOccupancy;
    private Integer capacity;
    private Double occupancyPercentage;
}
