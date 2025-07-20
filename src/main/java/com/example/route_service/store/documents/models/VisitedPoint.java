package com.example.route_service.store.documents.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

/**
 * Представляет собой информацию о посещении конкретной точки в рамках прохождения маршрута (PassageDocument)
 * Встраивается в список {@code visitedPoints} класса {@link com.example.route_service.store.documents.PointDocument}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VisitedPoint {
    /**
     * Идентификатор посещенной точки (ссылается на PointDocument)
     */
    @Field("point_id")
    @NotBlank
    String pointId;

    /**
     * Время прихода на точку
     */
    @Field("entry_time")
    @NotNull
    Instant entryTime;

    /**
     * Время ухода с точки
     */
    @Field("exit_time")
    Instant exitTime;

//    /**
//     * Статус посещения точки (VISITED, SKIPPED)
//     */
//    @Field("point_status")
//    PointStatus pointStatus;

//    /**
//     * Отзыв, оставленный пользователем о точке
//     * @see Feedback
//     */
//    @Field("feedback")
//    Feedback feedback;
}
