package com.example.route_service.api.services;


import com.example.route_service.api.dto.RouteDto;
import com.example.route_service.api.exeptions.InvalidObjectIdException;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.ObjectSaveException;
import com.example.route_service.api.mappers.RouteMapper;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.repositories.PointRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteService {
    RouteRepository routeRepository;
    PointRepository pointRepository;
    AuthService authService;


    public List<RouteDocument> getRoutes() {
        String userId = authService.getCurrentUserId();
        return routeRepository.findByUserId(userId);
    }

    public RouteDocument addRoute(RouteDto route) {
        String userId = authService.getCurrentUserId();
        validatePoints(route.getPointsId());
        try {
            return routeRepository.save(RouteMapper.toDocument(route, userId));
        } catch (Exception e) {
            throw new ObjectSaveException("Error adding route");
        }
    }

    public RouteDocument updateRoute(String routeId, RouteDto newRoute) {
        String userId = authService.getCurrentUserId();
        validatePoints(newRoute.getPointsId());

        RouteDocument route = routeRepository.findByUserIdAndRouteId(userId, routeId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        String.format("Route %s not found", routeId)));
        route.setPointsId(newRoute.getPointsId());
        return routeRepository.save(route);
    }

    public void deleteRoute(String routeId) {
        String userId = authService.getCurrentUserId();
        if (routeId == null || routeId.isBlank()) {
            throw new InvalidObjectIdException("routeId can't be null");
        }
        RouteDocument route = routeRepository.findByUserIdAndRouteId(userId, routeId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        String.format("Route %s not found", routeId)));
        routeRepository.delete(route);
    }

    public RouteDocument changeVisibility(String routeId) {
        String userId = authService.getCurrentUserId();
        RouteDocument route = routeRepository.findByUserIdAndRouteId(userId, routeId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        String.format("Route %s not found", routeId)));
        route.setPublic(!route.isPublic());
        return routeRepository.save(route);
    }

    private void validatePoints(List<String> pointsId) {
//        long existingPointsCount = pointRepository.countByPointIdIn(pointsId);
//        if (existingPointsCount != pointsId.size()) {
//            throw new InvalidObjectIdException("One or more points do not exist in the database");
//        }
        Set<String> uniquePoints = new HashSet<>(pointsId);

        // Получаем количество существующих точек из уникальных
        long existingPointsCount = pointRepository.countByPointIdIn(new ArrayList<>(uniquePoints));

        // Если количество существующих точек меньше уникальных, значит, не все точки есть в базе
        if (existingPointsCount != uniquePoints.size()) {
            throw new InvalidObjectIdException("One or more points do not exist in the database");
        }
    }

    public List<RouteDocument> getUserRoutes(String userId) {
        return routeRepository.findAllByUserIdAndIsPublicTrue(userId);
    }
}