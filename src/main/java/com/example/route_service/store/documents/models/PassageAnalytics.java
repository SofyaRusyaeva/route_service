package com.example.route_service.store.documents.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

/**
 * Класс, содержащий аналитику для одного конкретного прохождения.
 * Встраивается в {@link com.example.route_service.store.documents.PassageDocument}.
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassageAnalytics {

    /**
     * Покрытие маршрута (процент посещения точек запланированного маршрута)
     */
    double coverage;
    /**
     * Показатель соблюдения порядка (процент точек, посещенных в правильном порядке)
     */
    double order;

    /**
     * Список id пропущенных точек маршрута
     */
    List<String> missedPoints;

    /**
     * Список id точек, посещенных вне маршрута
     */
    List<String> extraPoints;

    /**
     * Список id точек, посещенных не по порядку
     */
    List<String> outOfOrderPoints;
}
