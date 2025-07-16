package com.example.route_service.api.dto;

import com.example.route_service.store.documents.models.LocationData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Точка на карте")
public class PointRequest {

    @Schema(description = "Тип точки, определяющий ее категорию", example = "Парк")
    @NotBlank(message = "Type cannot be null")
    String type;

    @Schema(description = "Гео-координаты точки")
    @NotNull(message = "Location cannot be null")
    GeoJsonPoint location;

    @Schema(description = "Полный строковый адрес точки", example = "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара")
    String address;

    @Schema(description = "Дополнительные данные о местоположении")
    @Valid
    LocationData locationData;
}