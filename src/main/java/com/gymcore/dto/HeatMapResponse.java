package com.gymcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HeatMapResponse {
    private int dayOfWeek;
    private int hour;
    private long checkInCount;
}
