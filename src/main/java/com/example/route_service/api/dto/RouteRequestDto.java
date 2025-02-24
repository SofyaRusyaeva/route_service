package com.example.route_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class RouteRequestDto {
    @NotBlank(message = "user_id cannot be null")
    String userId;

    @Getter
    @NotEmpty(message = "Points list cannot be empty")
    @Setter
    List<String> pointsId;

    public List<String> getPointsId() {
        return pointsId;
    }
}