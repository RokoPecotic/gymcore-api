package com.gymcore.analytics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class FranchiseAnalyticsResponse {
    private Integer totalLocations;
    private Long totalMembers;
    private String mostPopularLocation;
    private String leastPopularLocation;
    private Double averageOccupancyPerLocation;
    private List<LocationAnalyticsResponse> locations;
}
