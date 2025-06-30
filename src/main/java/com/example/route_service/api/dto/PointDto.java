package com.example.route_service.api.dto;

import com.example.route_service.store.documents.models.LocationData;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Точка на карте")
public class PointDto {

    @Schema(description = "Тип точки, определяющий ее категорию", example = "Парк")
    @NotBlank(message = "Type cannot be null")
    String type;

    @Schema(description = "Широта в градусах, диапазон от -90 до 90", example = "53.228981")
    @NotNull(message = "Latitude cannot be null")
    @Min(value = -90)
    @Max(value = 90)
    Double latitude;

    @Schema(description = "Долгота в градусах, диапазон от -180 до 180", example = "50.169918")
    @NotNull(message = "Longitude cannot be null")
    @Min(value = -180)
    @Max(value = 180)
    Double longitude;

    @Schema(description = "Полный строковый адрес точки", example = "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара")
    String address;

    @Schema(description = "Дополнительные данные о местоположении")
    @Valid
    LocationData locationData;
}