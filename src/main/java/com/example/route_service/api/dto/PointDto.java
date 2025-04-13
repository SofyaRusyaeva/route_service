package com.example.route_service.api.dto;

import com.example.route_service.store.documents.models.LocationData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointDto {

    @NotBlank(message = "Type cannot be null")
    String type;

    @NotNull(message = "Latitude cannot be null")
    @Min(value = -90)
    @Max(value = 90)
    Double latitude;

    @NotNull(message = "Longitude cannot be null")
    @Min(value = -180)
    @Max(value = 180)
    Double longitude;

    String address;

    @Valid
    LocationData locationData;
}