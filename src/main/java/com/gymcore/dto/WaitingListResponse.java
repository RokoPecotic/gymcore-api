package com.gymcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class WaitingListResponse {

    private Long id;
    private Long memberId;
    private Long locationId;
    private String locationName;
    private Boolean active;
    private LocalDateTime createdAt;
}
