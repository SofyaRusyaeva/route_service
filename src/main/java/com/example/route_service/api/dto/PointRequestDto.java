package com.example.route_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PointRequestDto {

    @NotBlank(message = "Type cannot be null")
    String type;

    @NotNull(message = "Latitude cannot be null")
    Double latitude;

    @NotNull(message = "Longitude cannot be null")
    Double longitude;

    String address;
}