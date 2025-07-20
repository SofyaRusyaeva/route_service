package com.example.route_service.api.dto;

import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.enums.PassageStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

/**
 * DTO для представления полной информации о конкретном прохождении маршрута
 *
 * @see com.example.route_service.store.documents.PassageDocument
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassageResponse {
    /**
     * Уникальный идентификатор прохождения
     */
    String passageId;
    /**
     * Идентификатор пользователя, проходящего маршрут
     */
    String userId;
    /**
     * Идентификатор проходимого маршрута
     */
    String routeId;
    /**
     * Текущий статус прохождения (IN_PROGRESS, COMPLETED, CANCELLED)
     */
    PassageStatus status;
    /**
     * Время начала прохождения маршрута
     */
    Instant startTime;
    /**
     * Время окончания прохождения маршрута (устанавливается при завершении или отмене)
     */
    Instant endTime;
    /**
     * Список посещенных точек с характеристиками по каждой
     * @see VisitedPoint
     */
    List<VisitedPoint> visitedPoints;
    /**
     * Отзыв, оставленный пользователем по прохождении маршрута
     * @see Feedback
     */
    Feedback feedback;
}
