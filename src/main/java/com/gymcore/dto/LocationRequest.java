package com.gymcore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotNull
    private Integer totalAreaM2;

    @NotNull
    private Integer capacity;


    private String email;

    @NotNull
    private Long tenantId;

}
