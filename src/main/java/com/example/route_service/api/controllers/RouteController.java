package com.example.route_service.api.controllers;


import com.example.route_service.api.dto.RouteRequest;
import com.example.route_service.api.dto.RouteResponse;
import com.example.route_service.api.services.RouteService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST-контроллер для управления маршрутами
 * Предоставляет эндпоинты для создания, чтения, обновления и удаления маршрутов,
 * а также для управления их видимостью.
 */
@RestController
@RequestMapping("api/routes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteController {

    RouteService routeService;

    /**
     * Получает все маршруты, принадлежащие текущему аутентифицированному пользователю
     *
     * @return {@link ResponseEntity} с кодом 200 OK и списком {@link RouteResponse} в теле
     * Если у пользователя нет маршрутов, возвращает пустой список
     */
    @GetMapping()
    public ResponseEntity<List<RouteResponse>> getMyRoutes() {
        return ResponseEntity.ok(routeService.getMyRoutes());
    }

    /**
     * Получает все публичные маршруты указанного пользователя
     * @param userId Уникальный идентификатор пользователя
     * @return {@link ResponseEntity} с кодом 200 OK и списком {@link RouteResponse}
     * Если у пользователя нет публичных маршрутов, возвращает пустой список
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<RouteResponse>> getUserRoutes(@PathVariable String userId) {
        return ResponseEntity.ok(routeService.getUserRoutes(userId));
    }


    /**
     * Создает новый маршрут для текущего пользователя
     * @param route Объект {@link RouteRequest} с данными для создания маршрута
     * @return {@link ResponseEntity} с кодом 201 Created и созданным {@link RouteResponse} в теле
     * @throws com.example.route_service.api.exeptions.InvalidObjectIdException если один или несколько
     *         id точек в запросе не существуют в БД (HTTP статус 400 Bad Request)
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если тело запроса не проходит
     *         валидацию (например, пустое поле) (HTTP статус 400 Bad Request)
     */
    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(@Valid @RequestBody RouteRequest route) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.addRoute(route));
    }

    /**
     * Обновляет существующий маршрут (точки, описание, флаг публичности)
     * @param routeId Идентификатор обновляемого маршрута
     * @param newRoute Объект {@link RouteRequest} с новыми данными
     * @return {@link ResponseEntity} с кодом 200 OK и обновленным {@link RouteResponse}
     * @throws com.example.route_service.api.exeptions.ObjectNotFoundException если маршрут не найден
     *         или не принадлежит текущему пользователю (HTTP статус 404 Not Found)
     * @throws com.example.route_service.api.exeptions.InvalidObjectIdException если один или несколько
     *         id точек в запросе не существуют (HTTP статус 400 Bad Request)
     */
    @PatchMapping("/{routeId}/points")
    public ResponseEntity<RouteResponse> updateRoutePoints(@Valid @PathVariable String routeId, @Valid @RequestBody RouteRequest newRoute) {
        return ResponseEntity.ok(routeService.updateRoute(routeId, newRoute));
    }

    /**
     * Изменяет флаг публичности маршрута на противоположный
     * @param routeId Идентификатор маршрута, у которого меняется видимость
     * @return {@link ResponseEntity} с кодом 200 OK и обновленным {@link RouteResponse}
     * @throws com.example.route_service.api.exeptions.ObjectNotFoundException если маршрут не найден
     *         или не принадлежит текущему пользователю (HTTP статус 404 Not Found)
     */
    @PatchMapping("/{routeId}/visibility")
    public ResponseEntity<RouteResponse> changeRouteVisibility(@PathVariable String routeId) {
        return ResponseEntity.ok(routeService.changeVisibility(routeId));
    }

    /**
     * Удаляет маршрут по его идентификатору
     * @param routeId Идентификатор удаляемого маршрута
     * @return {@link ResponseEntity} с кодом 200 OK и сообщением об успехе.
     * @throws com.example.route_service.api.exeptions.ObjectNotFoundException если маршрут не найден
     *         или не принадлежит текущему пользователю (HTTP статус 404 Not Found)
     */
    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable String routeId) {
        routeService.deleteRoute(routeId);
        return ResponseEntity.ok(String.format("Route %s deleted", routeId));
    }
}