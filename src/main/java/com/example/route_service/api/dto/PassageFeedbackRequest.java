package com.example.route_service.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * DTO для отправки отзыва о прохождении маршрута
 * Используется для передачи оценки и комментария
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassageFeedbackRequest {

    /**
     * Оценка, оставленная пользователем от 1 до 5
     */
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    Integer rating;

    /**
     * Текстовый комментарий пользователя
     */
    String comment;
}