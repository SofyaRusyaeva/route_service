package com.example.route_service.store.documents;

import com.example.route_service.store.documents.models.LocationData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Документ точки на карте, хранимый в коллекции "point"
 * Точка является частью маршрута
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "point")
public class PointDocument {

    /**
     * Уникальный идентификатор точки
     */
    @Id
    String pointId;

    /**
     * Тип точки
     */
    @Field("type")
    @NotBlank(message = "Point type cannot be null")
    String type;

    /**
     * Географические координаты точки в формате GeoJson
     */
    @Field("location")
    GeoJsonPoint location;

    /**
     * Адрес точки
     */
    @Field("address")
    String address;

    /**
     * Вложенный объект с дополнительными сведениями о точке (название, комментарий)
     *
     * @see LocationData
     */
    @Valid
    @Field("location_data")
    LocationData locationData;
}