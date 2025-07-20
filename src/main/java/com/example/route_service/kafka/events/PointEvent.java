package com.example.route_service.kafka.events;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

/**
 * Событие, связанное с взаимодействием пользователя с точкой на маршруте
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointEvent {
    /**
     * Тип произошедшего события
     * Ожидаемые значения: "POINT_ARRIVED", "POINT_DEPARTED"
     */
    String eventType; // POINT_ARRIVED, POINT_DEPARTED
    /**
     * Уникальный идентификатор прохождения
     * Связывает событие с конкретным документом {@link com.example.route_service.store.documents.PassageDocument}
     */
    String passageId;
    /**
     * Уникальный идентификатор точки, к которой относится событие (например, точка прибытия)
     * Связывает событие с конкретным документом {@link com.example.route_service.store.documents.PointDocument}
     */
    String pointId;
    /**
     * Время, когда произошло событие
     */
    Instant timestamp;
}