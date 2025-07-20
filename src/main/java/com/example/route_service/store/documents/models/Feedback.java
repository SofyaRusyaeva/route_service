package com.example.route_service.store.documents.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Класс для хранения отзыва пользователя, включающего оценку и комментарий
 * Встраивается в {@link com.example.route_service.store.documents.PassageDocument}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback {
    /**
     * Оценка, оставленная пользователем от 1 до 5
     */
    @Min(1)
    @Max(5)
    @Field("rating")
    Integer rating;

    /**
     * Текстовый комментарий пользователя
     */
    @Field("comment")
    String comment;
}