package com.gymcore.service;


import com.gymcore.dto.CheckInRequest;
import com.gymcore.dto.CheckInResponse;
import com.gymcore.dto.OccupancyResponse;
import com.gymcore.dto.HeatMapResponse;
import com.gymcore.entity.CheckIn;
import com.gymcore.entity.Location;
import com.gymcore.entity.Member;
import com.gymcore.exception.DuplicateResourceException;
import com.gymcore.exception.ResourceNotFoundException;
import com.gymcore.repository.CheckInRepository;
import com.gymcore.repository.LocationRepository;
import com.gymcore.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckInService {
    private final CheckInRepository checkInRepository;
    private final MemberRepository memberRepository;
    private final LocationRepository locationRepository;

    public CheckInResponse checkIn(CheckInRequest request){
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member not found with id " + request.getMemberId()
                ));

        Location location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id " + request.getLocationId()
                ));


        checkInRepository.findByMemberIdAndCheckOutTimeIsNull(request.getMemberId())
                .ifPresent(c -> {throw new DuplicateResourceException(
                        "Member already checked in");
                });

        CheckIn checkIn = new CheckIn();
        checkIn.setMember(member);
        checkIn.setLocation(location);
        checkIn.setCheckInTime(LocalDateTime.now());

        return toResponse(checkInRepository.save(checkIn));
    }

    public CheckInResponse checkOut(Long memberId){
        CheckIn checkIn = checkInRepository
                .findByMemberIdAndCheckOutTimeIsNull(memberId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Member is not checked in"
                ));
        checkIn.setCheckOutTime(LocalDateTime.now());
        return toResponse(checkInRepository.save(checkIn));
    }

    private CheckInResponse toResponse(CheckIn checkIn){
        return new CheckInResponse(
                checkIn.getId(),
                checkIn.getMember().getId(),
                checkIn.getLocation().getId(),
                checkIn.getCheckInTime(),
                checkIn.getCheckOutTime()
        );
    }
    public OccupancyResponse getOccupancy(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Location not found with id: " + locationId));

        long current = checkInRepository.countByLocationIdAndCheckOutTimeIsNull(locationId);
        double percentage = (double) current / location.getCapacity() * 100;

        return new OccupancyResponse(
                locationId,
                location.getName(),
                (int) current,
                location.getCapacity(),
                Math.round(percentage * 10.0) / 10.0
        );
    }

    public List<CheckInResponse> getMemberHistory(Long memberId) {
        return checkInRepository.findByMemberId(memberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<HeatMapResponse> getHeatmap(Long locationId) {
        LocalDateTime from = LocalDateTime.now().minusWeeks(8);
        LocalDateTime to = LocalDateTime.now();

        List<CheckIn> checkIns = checkInRepository
                .findByLocationIdAndTimeRange(locationId, from, to);

        Map<String, Long> counts = checkIns.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCheckInTime().getDayOfWeek().getValue()
                                + "-" + c.getCheckInTime().getHour(),
                        Collectors.counting()
                ));

        return counts.entrySet().stream()
                .map(e -> {
                    String[] parts = e.getKey().split("-");
                    return new HeatMapResponse(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            e.getValue()
                    );
                })
                .sorted(Comparator.comparingInt(HeatMapResponse::getDayOfWeek)
                        .thenComparingInt(HeatMapResponse::getHour))
                .toList();
    }
}
