package com.gymcore.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WaitingListRequest {
    @NotNull
    private Long memberId;

    @NotNull
    private Long locationId;
}
