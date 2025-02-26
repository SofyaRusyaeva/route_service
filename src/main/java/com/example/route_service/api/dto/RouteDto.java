package com.example.route_service.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteDto {
    @NotBlank(message = "user_id cannot be null")
    String userId;

    @Getter
    @NotEmpty(message = "Points list cannot be empty")
    @Setter
    List<String> pointsId;
}