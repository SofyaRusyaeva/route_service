package com.example.route_service.api.services;

import com.example.route_service.api.dto.PointRequest;
import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.StateException;
import com.example.route_service.api.mappers.PointMapper;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.repositories.PointRepository;
import com.example.route_service.store.repositories.RouteRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

/**
 * Сервис описывающий логику для работы с точками
 * Предоставляет методы для создания, чтения, обновления и удаления точек
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PointService {
    PointRepository pointRepository;
    RouteRepository routeRepository;
    PointMapper pointMapper;

    /**
     * Получает информацию о точке по её id
     *
     * @param pointId Уникальный идентификатор искомой точки
     * @return Объект {@link PointResponse} с данными о точке
     * @throws ObjectNotFoundException если точка с указанным id не найдена
     */
    public PointResponse getPointById(String pointId) {
        PointDocument point = pointRepository.findById(pointId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Point with id %s not found", pointId)));
        return pointMapper.toResponse(point);
    }

    /**
     * Обновляет данные существующей точки
     * @param pointId  Уникальный идентификатор обновляемой точки
     * @param newPoint Объект {@link PointRequest} с новыми данными для точки
     * @return Объект {@link PointResponse} с обновленными данными точки
     * @throws ObjectNotFoundException если точка с указанным id не найдена
     */
    public PointResponse updatePoint(String pointId, @Valid PointRequest newPoint) {
        PointDocument existingPoint = pointRepository.findById(pointId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Point with id %s not found", pointId)));

        existingPoint.setType(newPoint.getType());
        existingPoint.setLocation(newPoint.getLocation());
        existingPoint.setAddress(newPoint.getAddress());
        existingPoint.setLocationData(newPoint.getLocationData());

        PointDocument updatedPoint = pointRepository.save(existingPoint);
        return pointMapper.toResponse(updatedPoint);
    }

    /**
     * Создает новую точку на основе переданных данных
     * @param point Объект {@link PointRequest} с данными для создания новой точки
     * @return Объект {@link PointResponse} с данными созданной точки
     */
    public PointResponse addPoint(@Valid PointRequest point) {
        PointDocument pointDocument = pointRepository.save(pointMapper.toDocument(point));
        return pointMapper.toResponse(pointDocument);
    }

    /**
     * Удаляет точку по её идентификатору
     * Перед удалением выполняется проверка, что точка не используется ни в одном из существующих маршрутов
     * @param pointId Уникальный идентификатор удаляемой точки
     * @throws ObjectNotFoundException если точка с указанным id не найдена
     * @throws StateException если точка используется хотя бы в одном маршруте и не может быть удалена
     */
    public void deletePoint(String pointId) {
        if (!pointRepository.existsById(pointId)) {
            throw new ObjectNotFoundException(String.format("Point with id %s not found", pointId));
        }

        if (routeRepository.existsByPointsIdContains(pointId)) {
            throw new StateException(String.format("Point %s is in a route", pointId));
        }
        pointRepository.deleteById(pointId);
    }
}