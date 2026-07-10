package com.gymcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CheckInResponse {
    private Long id;
    private Long memberId;
    private Long locationId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}
