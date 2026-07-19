package com.gymcore.repository;

import com.gymcore.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnalyticsRepository extends JpaRepository<CheckIn, Long> {

    @Query("SELECT COUNT(c) FROM CheckIn c WHERE c.location.id = :locationId")
    Long countTotalVisits(@Param("locationId") Long locationId);

    @Query(value = "SELECT EXTRACT(HOUR FROM check_in_time), COUNT(*) " +
            "FROM check_ins WHERE location_id = :locationId " +
            "GROUP BY EXTRACT(HOUR FROM check_in_time) " +
            "ORDER BY COUNT(*) DESC",
            nativeQuery = true)
    List<Object[]> findHourlyDistribution(@Param("locationId") Long locationId);

    @Query(value = "SELECT EXTRACT(DOW FROM check_in_time), COUNT(*) " +
            "FROM check_ins WHERE location_id = :locationId " +
            "GROUP BY EXTRACT(DOW FROM check_in_time) " +
            "ORDER BY COUNT(*) DESC",
            nativeQuery = true)
    List<Object[]> findDailyDistribution(@Param("locationId") Long locationId);

    @Query(value = "SELECT AVG(EXTRACT(EPOCH FROM (check_out_time - check_in_time)) / 60) " +
            "FROM check_ins WHERE location_id = :locationId " +
            "AND check_out_time IS NOT NULL",
            nativeQuery = true)
    Double findAverageVisitDuration(@Param("locationId") Long locationId);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.location.tenant.id = :tenantId")
    Long countTotalMembers(@Param("tenantId") Long tenantId);

    @Query(value = "SELECT COUNT(c.id) * 1.0 / l.capacity * 100 " +
            "FROM check_ins c " +
            "JOIN locations l ON l.id = c.location_id " +
            "WHERE c.location_id = :locationId " +
            "GROUP BY l.capacity",
            nativeQuery = true)
    Double findAverageOccupancy(@Param("locationId") Long locationId);
}