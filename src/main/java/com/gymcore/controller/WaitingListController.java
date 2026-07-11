package com.gymcore.controller;

import com.gymcore.dto.WaitingListRequest;
import com.gymcore.dto.WaitingListResponse;
import com.gymcore.service.WaitingListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waitinglist")
@RequiredArgsConstructor
public class WaitingListController {

    private final WaitingListService waitingListService;

    @PostMapping
    public ResponseEntity<WaitingListResponse> joinWaitingList(
            @Valid @RequestBody WaitingListRequest request) {
        return ResponseEntity.ok(waitingListService.joinWaitingList(request));
    }

    @DeleteMapping("/{memberId}/{locationId}")
    public ResponseEntity<Void> leaveWaitingList(
            @PathVariable Long memberId,
            @PathVariable Long locationId) {
        waitingListService.leaveWaitingList(memberId, locationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<WaitingListResponse>> getWaitingListForLocation(
            @PathVariable Long locationId){
        return ResponseEntity.ok(waitingListService.getWaitingListForLocation(locationId));
    }
}
