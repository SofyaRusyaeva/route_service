package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.PointRequest;
import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.services.PointService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для управления точками
 * Предоставляет эндпоинты для создания, чтения, обновления и удаления точек
 */
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PointController {

    PointService pointService;

    /**
     * Получает информацию о точке по её id
     *
     * @param pointId Уникальный идентификатор искомой точки
     * @return {@link ResponseEntity} с кодом 200 OK и объектом {@link PointResponse} в теле
     * @throws com.example.route_service.api.exeptions.ObjectNotFoundException если точка
     *                                                                         с указанным id не найдена (HTTP статус 404 Not Found)
     */
    @GetMapping("/{pointId}")
    public ResponseEntity<PointResponse> getPointById(@PathVariable String pointId) {
        return ResponseEntity.ok(pointService.getPointById(pointId));
    }

    /**
     * Полностью обновляет существующую точку, заменяя её данные на переданные в запросе
     * @param pointId Идентификатор обновляемой точки
     * @param point Объект {@link PointRequest} с новыми данными точки
     * @return {@link ResponseEntity} с кодом 200 OK и обновленным {@link PointResponse}
     * @throws com.example.route_service.api.exeptions.ObjectNotFoundException если точка
     *         с указанным id не найдена (HTTP статус 404 Not Found)
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если тело запроса
     *         не проходит валидацию(HTTP статус 400 Bad Request)
     */
    @PutMapping("/{pointId}")
    public ResponseEntity<PointResponse> updatePoint(@PathVariable String pointId, @Valid @RequestBody PointRequest point) {
        return ResponseEntity.ok(pointService.updatePoint(pointId, point));
    }

    /**
     * Создает новую точку
     * @param point Объект {@link PointRequest} с данными для создания новой точки
     * @return {@link ResponseEntity} с кодом 201 Created и созданным {@link PointResponse}
     * @throws org.springframework.web.bind.MethodArgumentNotValidException если тело запроса
     *         не проходит валидацию (HTTP статус 400 Bad Request)
     */
    @PostMapping
    public ResponseEntity<PointResponse> addPoint(@Valid @RequestBody PointRequest point) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pointService.addPoint(point));
    }

    /**
     * Удаляет точку по её идентификатору
     * Удаление не будет выполнено, если точка является частью хотя бы одного маршрута
     * @param pointId Уникальный идентификатор удаляемой точки
     * @return {@link ResponseEntity} с кодом 204 No Content
     * @throws com.example.route_service.api.exeptions.ObjectNotFoundException если точка
     *         с указанным id не найдена (HTTP статус 404 Not Found)
     * @throws com.example.route_service.api.exeptions.StateException если точка используется в маршруте
     *         и не может быть удалена (HTTP статус 409 Conflict)
     */
    @DeleteMapping("/{pointId}")
    public ResponseEntity<Void> deletePoint(@PathVariable String pointId) {
        pointService.deletePoint(pointId);
        return ResponseEntity.noContent().build();
    }
}
