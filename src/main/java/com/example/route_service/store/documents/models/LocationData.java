package com.example.route_service.store.documents.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Дополнительные данные о точке")
public class LocationData {

    @Schema(description = "Краткое название места", example = "Парк культуры и отдыха имени Ю. А. Гагарина")
    @NotBlank(message = "Point name cannot be null")
    @Field("name")
    String name;

    @Schema(description = "Отзыв, комментарий или краткое описание локации", example = "Парк культуры и отдыха имени Ю. А. Гагарина — это популярное место для семейного отдыха в Самаре")
    @Field("review")
    String review;

    @Schema(
            description = "Карта атрибутов, представляющих дополнительную информацию. Ключ - название атрибута, значение - его содержание",
            example = "{\"website\": \"https://parki-samara.ru/park-im-yu-gagarina/\", \"opening_hours\": \"Круглосуточно\"}"
    )
    @Field("attributions")
    HashMap<String, String> attributions;
}