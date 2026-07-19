package com.gymcore.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CapacityInsightResponse {
    private Long locationId;
    private String locationName;
    private Integer capacity;
    private Double averageOccupancy;
    private String status;
}
