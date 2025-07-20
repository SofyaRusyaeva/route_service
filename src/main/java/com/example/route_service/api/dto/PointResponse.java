package com.example.route_service.api.dto;

import com.example.route_service.store.documents.models.LocationData;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * DTO для представления информации о точке на карте в ответе API.
 *
 * @see com.example.route_service.store.documents.PointDocument
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointResponse {

    /**
     * Уникальный идентификатор точки
     */
    String pointId;

    /**
     * Тип точки
     */
    String type;

    /**
     * Географические координаты точки в формате GeoJson
     */
    GeoJsonPoint location;

    /**
     * Адрес точки
     */
    String address;

    /**
     * Вложенный объект с дополнительными сведениями о точке (название, комментарий)
     * @see LocationData
     */
    LocationData locationData;
}
