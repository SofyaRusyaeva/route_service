package com.example.route_service.api.services;

import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.dto.RouteRequest;
import com.example.route_service.api.dto.RouteResponse;
import com.example.route_service.api.exeptions.InvalidObjectIdException;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.mappers.PointMapper;
import com.example.route_service.api.mappers.RouteMapper;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.repositories.PointRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис описывающий логику для работы с маршрутами
 * Отвечает за создание, чтение, обновление, удаление и изменение видимости маршрутов
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteService {
    RouteRepository routeRepository;
    PointRepository pointRepository;
    AuthService authService;
    PointMapper pointMapper;
    RouteMapper routeMapper;

    /**
     * Получает список всех маршрутов, созданных текущим аутентифицированным пользователем
     *
     * @return Список Объект {@link RouteResponse} с информацией о маршрутах пользователя
     */
    public List<RouteResponse> getMyRoutes() {
        String userId = authService.getCurrentUserId();
        List<RouteDocument> routes = routeRepository.findByUserId(userId);
        return buildRouteResponse(routes);
    }

    /**
     * Получает список всех публичных маршрутов, созданных указанным пользователем
     * @param userId Уникальный идентификатор пользователя, чьи маршруты нужно найти
     * @return Список Объект {@link  RouteResponse} с информацией о публичных маршрутах пользователя
     */
    public List<RouteResponse> getUserRoutes(String userId) {
        List<RouteDocument> routes = routeRepository.findAllByUserIdAndIsPublicTrue(userId);
//        return routes.stream()
//                .map(this::buildRouteResponse)
//                .toList();
        return buildRouteResponse(routes);
    }

    /**
     * Создает новый маршрут для текущего пользователя
     * @param request Объект {@link RouteRequest} с данными для создания маршрута
     * @return Объект {@link RouteResponse} с информацией о созданном маршруте
     * @throws InvalidObjectIdException если одна или несколько точек из запроса не существуют в базе данных
     */
    public RouteResponse addRoute(RouteRequest request) {
        String userId = authService.getCurrentUserId();
        List<PointDocument> points = validateAndGetPoints(request.getPointsId());

        RouteDocument route = routeMapper.toDocument(request);
        route.setUserId(userId);

        RouteDocument savedRoute = routeRepository.save(route);

        List<PointResponse> pointResponses = points.stream()
                .map(pointMapper::toResponse)
                .toList();

        return routeMapper.toResponse(savedRoute, pointResponses);

//        validatePoints(route.getPointsId());
//
//        RouteDocument routeDocument = routeMapper.toDocument(route);
//        routeDocument.setUserId(userId);
//
//        return buildRouteResponse(routeRepository.save(routeDocument));
    }

    /**
     * Обновляет существующий маршрут (состав точек, флаг публичности и описание)
     * Проверяет, что маршрут принадлежит текущему пользователю
     * @param routeId Уникальный идентификатор обновляемого маршрута
     * @param newRoute Объект {@link RouteRequest} с новыми данными для маршрута
     * @return Объект {@link RouteResponse}
     * @throws ObjectNotFoundException если маршрут с указанным id не найден у текущего пользователя
     * @throws InvalidObjectIdException если одна или несколько точек из запроса не существуют в базе данных
     */
    public RouteResponse updateRoute(String routeId, RouteRequest newRoute) {
        String userId = authService.getCurrentUserId();

        List<PointDocument> points = validateAndGetPoints(newRoute.getPointsId());

        RouteDocument route = routeRepository.findByUserIdAndRouteId(userId, routeId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Route %s not found", routeId)));

        route.setPointsId(newRoute.getPointsId());
        route.setPublic(newRoute.isPublic());
        route.setDescription(newRoute.getDescription());

        RouteDocument updatedRoute = routeRepository.save(route);

        List<PointResponse> pointResponses = points.stream()
                .map(pointMapper::toResponse)
                .toList();

        return routeMapper.toResponse(updatedRoute, pointResponses);
//        validatePoints(newRoute.getPointsId());
//
//        RouteDocument route = routeRepository.findByUserIdAndRouteId(userId, routeId)
//                .orElseThrow(() -> new ObjectNotFoundException(String.format("Route %s not found", routeId)));
//
//        route.setPointsId(newRoute.getPointsId());
//        route.setPublic(newRoute.isPublic());
//        route.setDescription(newRoute.getDescription());
//
//        return buildRouteResponse(routeRepository.save(route));
    }

    /**
     * Удаляет маршрут
     * Операция доступна только владельцу маршрута
     * @param routeId Уникальный идентификатор удаляемого маршрута
     * @throws ObjectNotFoundException если маршрут с указанным id не найден у текущего пользователя
     */
    public void deleteRoute(String routeId) {
        String userId = authService.getCurrentUserId();

        long deleteCount = routeRepository.deleteByRouteIdAndUserId(routeId, userId);

        if (deleteCount == 0) {
            throw new ObjectNotFoundException(String.format("Route %s not found", routeId));
        }
//        if (routeId == null || routeId.isBlank()) {
//            throw new InvalidObjectIdException("routeId can't be null");
//        }
//        RouteDocument route = routeRepository.findByUserIdAndRouteId(userId, routeId)
//                .orElseThrow(() -> new ObjectNotFoundException(
//                        String.format("Route %s not found", routeId)));
//        routeRepository.delete(route);
    }

    /**
     * Изменяет флаг публичности маршрута на противоположный
     * @param routeId Уникальный идентификатор маршрута.
     * @return Объект {@link RouteResponse} с обновленной информацией о маршруте
     * @throws ObjectNotFoundException если маршрут с указанным id не найден у текущего пользователя
     */
    public RouteResponse changeVisibility(String routeId) {
        String userId = authService.getCurrentUserId();
        RouteDocument route = routeRepository.findByUserIdAndRouteId(userId, routeId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Route %s not found", routeId)));
        route.setPublic(!route.isPublic());

        RouteDocument savedRoute = routeRepository.save(route);

        List<PointDocument> points = pointRepository.findAllById(savedRoute.getPointsId());
        List<PointResponse> pointResponses = points.stream()
                .map(pointMapper::toResponse)
                .toList();
        return routeMapper.toResponse(savedRoute, pointResponses);

//        return buildRouteResponse(routeRepository.save(route));
    }

//    private void validatePoints(List<String> pointsId) {
//        Set<String> uniquePoints = new HashSet<>(pointsId);
//
//        long existingPointsCount = pointRepository.countByPointIdIn(new ArrayList<>(uniquePoints));
//
//        if (existingPointsCount != uniquePoints.size()) {
//            throw new InvalidObjectIdException("One or more points do not exist in the database");
//        }
//    }

    /**
     * Приватный метод для проверки существования точек и их получения из БД
     * @param pointIds Список идентификаторов точек для проверки
     * @return Список {@link PointDocument} в исходном порядке
     * @throws InvalidObjectIdException если хотя бы одна из точек не найдена в базе данных
     */
    private List<PointDocument> validateAndGetPoints(List<String> pointIds) {
        if (pointIds.isEmpty()) {
            return Collections.emptyList();
        }
        Set<String> uniquePointIds = new HashSet<>(pointIds);
        List<PointDocument> points = pointRepository.findAllById(uniquePointIds);

        if (points.size() != uniquePointIds.size()) {
            Set<String> foundIds = points.stream().map(PointDocument::getPointId).collect(Collectors.toSet());
            List<String> missingIds = uniquePointIds.stream().filter(id -> !foundIds.contains(id)).toList();
            throw new InvalidObjectIdException("One or more points do not exist in the database" + missingIds);
        }

        Map<String, PointDocument> pointsMap = points.stream()
                .collect(Collectors.toMap(PointDocument::getPointId, Function.identity()));
        return pointIds.stream().map(pointsMap::get).toList();
    }


    /**
     * Приватный метод для сборки списка {@link RouteResponse}
     * Чтобы избежать проблемы N+1 запросов, метод сначала собирает все уникальные
     * идентификаторы точек из всех маршрутов, делает один запрос в БД для получения
     * всех точек, а затем распределяет их по соответствующим маршрутам
     * @param routes Список документов {@link RouteDocument}
     * @return Список объектов {@link RouteResponse} с заполненными данными о точках
     */
    private List<RouteResponse> buildRouteResponse(List<RouteDocument> routes) {
        if (routes.isEmpty()) {
            return Collections.emptyList();
        }

        Set<String> allPointIds = routes.stream()
                .flatMap(route -> route.getPointsId().stream())
                .collect(Collectors.toSet());

        Map<String, PointDocument> pointsMap = pointRepository.findAllById(allPointIds).stream()
                .collect(Collectors.toMap(PointDocument::getPointId, Function.identity()));

        return routes.stream().map(route -> {
                    List<PointResponse> pointResponses = route.getPointsId().stream()
                            .map(pointsMap::get)
                            .filter(Objects::nonNull)
                            .map(pointMapper::toResponse)
                            .toList();
                    return routeMapper.toResponse(route, pointResponses);
                })
                .toList();

//        List<PointDocument> points = pointRepository.findAllById(routeDocument.getPointsId());
//
//        List<PointResponse> pointResponses = points.stream()
//                .map(pointMapper::toResponse)
//                .toList();
//
//        return routeMapper.toResponse(routeDocument, pointResponses);
    }
}