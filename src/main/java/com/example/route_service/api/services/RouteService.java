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

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteService {
    RouteRepository routeRepository;
    PointRepository pointRepository;


    public List<RouteDocument> getRoutesByUserId(String userId) {
        List<RouteDocument> routes = routeRepository.findByUserId(userId);
        if(routes.isEmpty())
            throw new ObjectNotFoundException(String.format("No routes found for user %s", userId));
        return routes;
    }

    public RouteDocument addRoute(RouteDto route, String userId) {
        validatePoints(route.getPointsId());
        try {
            return routeRepository.save(RouteMapper.toDocument(route, userId));
        } catch (Exception e) {
            throw new ObjectSaveException("Error adding route");
        }
    }

    public RouteDocument updateRoute(String routeId, RouteDto newRoute) {
        validatePoints(newRoute.getPointsId());
        return routeRepository.findById(routeId)
                .map(existingRoute -> {
                    existingRoute.setPointsId(newRoute.getPointsId());
                    return routeRepository.save(existingRoute);
                })
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Route with id %s not found", routeId)));
    }

    public void deleteRoute(String routeId) {
        if (routeId == null || routeId.isBlank()) {
            throw new InvalidObjectIdException("routeId can't be null");
        }
        RouteDocument route = routeRepository.findById(routeId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Route with id %s not found", routeId))
        );
        routeRepository.delete(route);
    }

    private void validatePoints(List<String> pointsId) {
        long existingPointsCount = pointRepository.countByPointIdIn(pointsId);
        if (existingPointsCount != pointsId.size()) {
            throw new InvalidObjectIdException("One or more points do not exist in the database");
        }
    }
}
