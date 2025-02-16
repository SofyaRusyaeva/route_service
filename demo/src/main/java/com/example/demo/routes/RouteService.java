package com.example.demo.routes;

import com.example.demo.repositoties.PointRepository;
import com.example.demo.repositoties.RouteRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteService {

    private final RouteRepository routeRepository;
    private final PointRepository pointRepository;

    public RouteService(RouteRepository routeRepository, PointRepository pointRepository) {
        this.routeRepository = routeRepository;
        this.pointRepository = pointRepository;
    }

    public List<Route> getRoutesByUserId(String userId) {
        ObjectId objectId = new ObjectId(userId);
        return routeRepository.findByUserId(objectId);
    }

    public Route addRoute(Route route) {
        return routeRepository.save(route);
    }

    public Point getPointById(String pointId) {
        return pointRepository.findByPointId(pointId);
    }

    public Route updateRoute(String routeId, Route newRoute) {
        Optional<Route> oldRouteOpt = routeRepository.findById(routeId);
        if (oldRouteOpt.isPresent()) {
            Route oldRoute = oldRouteOpt.get();
            oldRoute.setPoints(newRoute.getPoints());
            return routeRepository.save(oldRoute);
        }
        return null;
    }
}