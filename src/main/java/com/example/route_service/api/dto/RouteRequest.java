package com.example.route_service.api.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;

/**
 * DTO с данными для создания нового маршрута
 *
 * @see com.example.route_service.store.documents.RouteDocument
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteRequest {
    /**
     * Список идентификаторов точек (PointDocument), составляющих маршрут
     */
    @NotEmpty(message = "Points list cannot be empty")
    List<String> pointsId;

    /**
     * Флаг публичности маршрута, указывающий, является ли маршрут
     * публичным и доступным для всех пользователей
     */
    boolean isPublic;

    /**
     * Дополнительные сведения о маршруте (название, описание, рейтинг и т.д.)
     */
    HashMap<String, String> description;
}