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

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteService {
    RouteRepository routeRepository;
    PointRepository pointRepository;
    AuthService authService;
    PointMapper pointMapper;
    RouteMapper routeMapper;


    public List<RouteResponse> getMyRoutes() {
        String userId = authService.getCurrentUserId();
        List<RouteDocument> routes = routeRepository.findByUserId(userId);
        return buildRouteResponse(routes);
    }

    public List<RouteResponse> getUserRoutes(String userId) {
        List<RouteDocument> routes = routeRepository.findAllByUserIdAndIsPublicTrue(userId);
//        return routes.stream()
//                .map(this::buildRouteResponse)
//                .toList();
        return buildRouteResponse(routes);
    }

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

    public RouteResponse updateRoute(String routeId, RouteRequest newRoute) {
        String userId = authService.getCurrentUserId();

        List<PointDocument> points = validateAndGetPoints(newRoute.getPointsId());

        RouteDocument route = routeRepository.findById(routeId)
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