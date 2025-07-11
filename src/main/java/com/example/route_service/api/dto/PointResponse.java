package com.example.route_service.api.dto;

import com.example.route_service.store.documents.models.LocationData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointResponse {

    @Schema(description = "id точки")
    String pointId;

    @Schema(description = "Тип точки, определяющий ее категорию", example = "Парк")
    String type;

    GeoJsonPoint location;

    @Schema(description = "Полный строковый адрес точки", example = "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара")
    String address;

    @Schema(description = "Дополнительные данные о местоположении")
    LocationData locationData;
}
