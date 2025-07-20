package com.example.route_service.api.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.List;

/**
 * DTO для представления полной информации о маршруте
 *
 * @see com.example.route_service.store.documents.RouteDocument
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteResponse {

    /**
     * Уникальный идентификатор маршрута
     */
    String routeId;

    /**
     * Идентификатор пользователя, создавшего маршрут
     */
    String userId;

    /**
     * Список идентификаторов точек (PointDocument), составляющих маршрут
     */
    List<PointResponse> points;

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
