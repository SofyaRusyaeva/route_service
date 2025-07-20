package com.example.route_service.api.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO для представления агрегированной и рассчитанной аналитики по маршруту
 * Содержит усредненные значения и относительные показатели (проценты)
 *
 * @see com.example.route_service.store.documents.models.RouteAnalytics
 */
@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteAnalyticsDto {

    /**
     * id маршрута, к которому относится аналитика
     */
    String routeId;
    /**
     * Общее количество стартов маршрута
     */
    long totalStarts;
//    long totalCompletions;
//    long totalCancellations;

    /**
     * Процент успешных завершений маршрута от общего числа стартов
     */
    Double completionsPercent;
    /**
     * Процент отмен маршрута от общего числа стартов
     */
    Double cancellationsPercent;

    /**
     * Средний рейтинг маршрута на основе всех оценок
     */
    Double avgRating;
    /**
     * Средняя продолжительность прохождения маршрута в секундах
     */
    Double avgDuration;
    /**
     * Средний процент посещенных точек от запланированных в маршруте
     */
    Double avgCoverage;
    /**
     * Средний процент точек, посещенных в правильном порядке
     */
    Double avgOrder;

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
     * Частота прохождения точек не по порядку
     * Ключ - id точки, значение - количество нарушений
     */
    Map<String, Long> outOfOrderPointsFrequency = new HashMap<>();
    /**
     * Среднее время, проведенное на каждой точке
     * Ключ - id точки, значение - средняя длительность в секундах
     */
    Map<String, Double> avgPointDurations = new HashMap<>();

}
