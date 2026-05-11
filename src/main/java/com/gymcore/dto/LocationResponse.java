package com.gymcore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String name;
    private String address;
    private String city;
    private Integer totalAreaM2;
    private Integer capacity;
    private String phoneNumber;
    private String email;
    private Boolean active;
    private Long tenantId;
    private LocalDateTime createdAt;
}
