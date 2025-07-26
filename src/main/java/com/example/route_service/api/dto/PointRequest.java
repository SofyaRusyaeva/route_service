package com.example.route_service.api.dto;

import com.example.route_service.store.documents.models.LocationData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

/**
 * DTO для создания новой точки
 *
 * @see com.example.route_service.store.documents.PointDocument
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointRequest {

    /**
     * Тип точки
     */
    @NotBlank(message = "Type cannot be null")
    String type;

    /**
     * Географические координаты точки в формате GeoJson
     */
    @NotNull(message = "Location cannot be null")
    GeoJsonPoint location;

    /**
     * Адрес точки
     */
    String address;

    /**
     * Вложенный объект с дополнительными сведениями о точке (название, комментарий)
     * @see LocationData
     */
    @Valid
    LocationData locationData;
}