package com.gymcore.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LocationAnalyticsResponse {
    private Long locationId;
    private String locationName;
    private Double averageOccupancy;
    private Integer peakHour;
    private String peakDay;
    private Long totalVisits;
    private Double averageVisitDurationMinutes;
}
