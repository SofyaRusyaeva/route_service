package com.example.route_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointDto {

    @NotBlank(message = "Type cannot be null")
    String type;

    @NotNull(message = "Latitude cannot be null")
    Double latitude;

    @NotNull(message = "Longitude cannot be null")
    Double longitude;

    String address;
}