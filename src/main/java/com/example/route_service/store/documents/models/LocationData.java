package com.example.route_service.store.documents.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;

/**
 * Дополнительные данные о локации, связанные с точкой {@link com.example.route_service.store.documents.PointDocument}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Schema(description = "Дополнительные данные о точке")
public class LocationData {

    /**
     * Краткое название места
     */
    @NotBlank(message = "Point name cannot be null")
    @Field("name")
    String name;

    /**
     * Отзыв, комментарий или краткое описание локации
     */
    @Field("review")
    String review;

    /**
     * Карта атрибутов, представляющих дополнительную информацию. Ключ - название атрибута, значение - его содержание
     */
    @Field("attributions")
    HashMap<String, String> attributions;
}