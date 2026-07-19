package com.gymcore.analytics;

import com.gymcore.entity.Location;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.AnalyticsRepository;
import com.gymcore.repository.LocationRepository;
import com.gymcore.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private final AnalyticsRepository analyticsRepository;
    private final LocationRepository locationRepository;
    private final TenantRepository tenantRepository;

    public LocationAnalyticsResponse getLocationAnalytics(Long locationId){
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + locationId
                ));
        Long totalVisits = analyticsRepository.countTotalVisits(locationId);
        List<Object[]> hourly = analyticsRepository.findHourlyDistribution(locationId);
        Integer peakHour = hourly.isEmpty() ? 0 :
                ((Number) hourly.get(0)[0]).intValue();
        List<Object[]> daily = analyticsRepository.findDailyDistribution(locationId);
        String peakDay = daily.isEmpty() ? "N/A" :
                getDayName(((Number) daily.get(0)[0]).intValue());
        Double avgDuration = analyticsRepository.findAverageVisitDuration(locationId);
        Double avgOccupancy = analyticsRepository.findAverageOccupancy(locationId);

        return new LocationAnalyticsResponse(
                locationId,
                location.getName(),
                avgOccupancy != null ? Math.round(avgOccupancy * 10.0) / 10.0 : 0.0,
                peakHour,
                peakDay,
                totalVisits,
                avgDuration != null ? Math.round(avgDuration * 10.0) / 10.0 : 0.0
        );
    }

    private String getDayName(int dow) {
        return switch(dow) {
            case 1 -> "MONDAY";
            case 2 -> "TUESDAY";
            case 3 -> "WEDNESDAY";
            case 4 -> "THURSDAY";
            case 5 -> "FRIDAY";
            case 6 -> "SATURDAY";
            case 7 -> "SUNDAY";
            default -> "UNKNOWN";
        };
    }

    public FranchiseAnalyticsResponse getFranchiseAnalytics(Long tenantId) {
        List<Location> locations = locationRepository.findByTenantId(tenantId);

        List<LocationAnalyticsResponse> locationAnalytics = locations.stream()
                .map(l -> getLocationAnalytics(l.getId()))
                .toList();

        String mostPopular = locationAnalytics.stream()
                .max(Comparator.comparingLong(LocationAnalyticsResponse::getTotalVisits))
                .map(LocationAnalyticsResponse::getLocationName)
                .orElse("N/A");

        String leastPopular = locationAnalytics.stream()
                .min(Comparator.comparingLong(LocationAnalyticsResponse::getTotalVisits))
                .map(LocationAnalyticsResponse::getLocationName)
                .orElse("N/A");

        Double avgOccupancy = locationAnalytics.stream()
                .mapToDouble(LocationAnalyticsResponse::getAverageOccupancy)
                .average()
                .orElse(0.0);

        Long totalMembers = analyticsRepository.countTotalMembers(tenantId);

        return new FranchiseAnalyticsResponse(
                locations.size(),
                totalMembers,
                mostPopular,
                leastPopular,
                Math.round(avgOccupancy * 10.0) / 10.0,
                locationAnalytics
        );
    }

    public List<CapacityInsightResponse> getCapacityInsights(Long tenantId) {
        List<Location> locations = locationRepository.findByTenantId(tenantId);

        return locations.stream()
                .map(l -> {
                    Double avg = analyticsRepository.findAverageOccupancy(l.getId());
                    double occupancy = avg != null ? avg : 0.0;

                    String status;
                    if (occupancy > 80) {
                        status = "OVERSATURATED";
                    } else if (occupancy >= 40) {
                        status = "OPTIMAL";
                    } else {
                        status = "UNDERUTILIZED";
                    }

                    return new CapacityInsightResponse(
                            l.getId(),
                            l.getName(),
                            l.getCapacity(),
                            Math.round(occupancy * 10.0) / 10.0,
                            status
                    );
                })
                .toList();
    }
}
