package com.example.route_service.api.services;


import com.example.route_service.api.exeptions.InvalidObjectIdException;
import com.example.route_service.api.exeptions.RouteNotFoundException;
import com.example.route_service.api.exeptions.RouteSaveException;
import com.example.route_service.store.documents.RouteDocument;
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

    public List<RouteDocument> getRoutesByUserId(String userId) {
        List<RouteDocument> routes = routeRepository.findByUserId(userId);
        if(routes.isEmpty())
            throw new RouteNotFoundException(String.format("No routes found for user %s", userId));
        return routes;
    }

    public RouteDocument addRoute(RouteDocument route) {
        try {
            return routeRepository.save(route);
        } catch (Exception e) {
            throw new RouteSaveException("Error adding route");
        }
    }

    public RouteDocument updateRoute(String routeId, RouteDocument newRoute) {
        return routeRepository.findById(routeId)
                .map(existingRoute -> {
                    existingRoute.setPointsId(newRoute.getPointsId());
                    return routeRepository.save(existingRoute);
                })
                .orElseThrow(() -> new RouteNotFoundException(String.format("Route with id %s not found", routeId)));
    }

    public void deleteRoute(String routeId) {
        if (routeId == null || routeId.isBlank()) {
            throw new InvalidObjectIdException("routeId can't be null");
        }
        RouteDocument route = routeRepository.findById(routeId).orElseThrow(
                () -> new RouteNotFoundException(String.format("Route with id %s not found", routeId))
        );
        routeRepository.delete(route);
    }
}
