package com.example.route_service.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteResponse {

    @Schema(description = "id маршрута")
    String routeId;

    @Schema(description = "id пользователя-владельца")
    String userId;

    @Schema(description = "Список точек маршрута")
    List<PointResponse> points;

    @Schema(description = "Флаг публичности маршрута")
    boolean isPublic;

    @Schema(description = "Дополнительные сведения")
    HashMap<String, String> description;
}
