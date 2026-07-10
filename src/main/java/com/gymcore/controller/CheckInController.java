package com.gymcore.controller;

import com.gymcore.dto.*;
import com.gymcore.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
@RequiredArgsConstructor
public class CheckInController {
    private final CheckInService checkInService;

    @PostMapping
    public ResponseEntity<CheckInResponse> checkIn(
            @Valid @RequestBody CheckInRequest request) {
        return ResponseEntity.ok(checkInService.checkIn(request));
    }

    @PutMapping("/checkout/{memberId}")
    public ResponseEntity<CheckInResponse> checkOut(
            @PathVariable Long memberId){
        return ResponseEntity.ok(checkInService.checkOut(memberId));
    }

    @GetMapping("/occupancy/{locationId}")
    public ResponseEntity<OccupancyResponse> getOccupancy(
            @PathVariable Long locationId){
        return ResponseEntity.ok(checkInService.getOccupancy(locationId));
    }

    @GetMapping("/history/{memberId}")
    public ResponseEntity<List<CheckInResponse>> getMemberHistory(
            @PathVariable Long memberId) {
        return ResponseEntity.ok(checkInService.getMemberHistory(memberId));
    }
}
