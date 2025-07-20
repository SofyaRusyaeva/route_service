package com.example.route_service.store.documents.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

/**
 * Содержит агрегированную аналитику по всем прохождениям данного маршрута
 * Встраивается в {@link com.example.route_service.store.documents.RouteDocument}.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteAnalytics {

    /**
     * Общее количество стартов маршрута
     */
    long totalStarts = 0;
    /**
     * Общее количество завершений прохождений
     */
    long totalCompletions = 0;
    /**
     * Общее количество отмен прохождений
     */
    long totalCancellations = 0;

    /**
     * Сумма всех оценок, оставленных пользователями
     */
    Double totalRating = 0.0;
    /**
     * Количество оставленных оценок, используется для расчета среднего рейтинга
     */
    long ratingsCount = 0;

    /**
     * Суммарная длительность всех завершенных прохождений в секундах
     */
    long totalDuration = 0;

    /**
     * Сумма всех показателей "покрытия" точек маршрута для расчета среднего
     */
    Double totalCoverage = 0.0;
    /**
     * Сумма всех показателей "порядка прохождения" точек маршрута для расчета среднего
     */
    Double totalOrder = 0.0;
//    Long passagesAnalyzedCount = 0L;

//    Double avgRating;
//    Double avgDuration;
//    Double avgCoverage;
//    Double avgOrder;
    /**
     * Частота пропуска каждой точки
     * Ключ - id точки, значение - количество пропусков
     */
    Map<String, Long> missedPointsFrequency = new HashMap<>();
    /**
     * Частота посещения лишних точек (не из маршрута)
     * Ключ - id точки, значение - количество посещений
     */
    Map<String, Long> extraPointsFrequency = new HashMap<>();
    /**
     * Частота прохождений точек не по порядку, указанному в маршруте
     * Ключ - id точки, значение - количество нарушений порядка
     */
    Map<String, Long> outOfOrderPointsFrequency = new HashMap<>();

    /**
     * Суммарное время, проведенное на каждой точке
     * Ключ - id точки, значение - суммарная длительность в секундах
     */
    Map<String, Long> totalPointDurations = new HashMap<>();
    /**
     * Количество посещений каждой точки
     * Ключ - pointId, значение - общее число посещений
     */
    Map<String, Long> pointVisitCount = new HashMap<>();

//    Map<String, Long> avgPointDurations = new HashMap<>();
}