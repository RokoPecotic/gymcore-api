package com.gymcore.repository;

import com.gymcore.entity.CheckIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CheckInRepository extends JpaRepository<CheckIn, Long> {

    Optional<CheckIn> findByMemberIdAndCheckOutTimeIsNull(Long memberId);

    List<CheckIn> findByLocationId(Long locationId);

    long countByLocationIdAndCheckOutTimeIsNull(Long locationId);

    List<CheckIn> findByMemberId(Long memberId);

    @Query("SELECT c FROM CheckIn c WHERE c.location.id = :locationId " +
    "AND c.checkInTime BETWEEN :from AND :to")
    List<CheckIn> findByLocationIdAndTimeRange(
            @Param("locationId") Long locationId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
