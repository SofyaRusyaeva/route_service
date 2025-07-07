package com.example.route_service.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;

// TODO подумать над DTO (возможно разделить на ответ и запрос)

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteDto {

    @NotEmpty(message = "Points list cannot be empty")
    List<String> pointsId;

    boolean isPublic;

    HashMap<String, String> description;
}