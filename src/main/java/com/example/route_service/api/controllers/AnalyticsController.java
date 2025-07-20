package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.RouteAnalyticsDto;
import com.example.route_service.api.services.AnalyticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер для предоставления аналитических данных по маршрутам
 */

@RestController
@RequestMapping("api/analytics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyticsController {

    AnalyticsService analyticsService;

    /**
     * Возвращает DTO, который содержит средние значения, проценты и другие
     * агрегированные метрики, вычисленные на основе всех прохождений данного маршрута
     *
     * @param routeId Уникальный идентификатор маршрута, для которого запрашивается аналитика
     * @return {@link ResponseEntity} с кодом 200 OK и объектом {@link RouteAnalyticsDto} в теле,
     * содержащим рассчитанные метрики
     * @throws com.example.route_service.api.exeptions.ObjectNotFoundException если маршрут
     *                                                                         с указанным id не найден (HTTP статус 404 Not Found)
     */
    @GetMapping("/{routeId}")
    public ResponseEntity<RouteAnalyticsDto> getAnalytics(@PathVariable String routeId) {
        return ResponseEntity.ok(analyticsService.getAnalytics(routeId));
    }
}
