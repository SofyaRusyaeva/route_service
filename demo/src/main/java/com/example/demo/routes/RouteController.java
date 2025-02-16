package com.example.demo.routes;

import com.example.demo.repositoties.PointRepository;
import com.example.demo.repositoties.RouteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {
    private final RouteRepository routeRepository;
    private final PointRepository pointRepository;

    private final RouteService routeService;

    public RouteController(RouteRepository routeRepository, PointRepository pointRepository, RouteService routeService) {
        this.routeRepository = routeRepository;
        this.pointRepository = pointRepository;
        this.routeService = routeService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserRoutes(@PathVariable String userId) {
        List<Route> routes = routeService.getRoutesByUserId(userId);
        if (routes.isEmpty())
            return ResponseEntity.notFound().build(); // 404 not found
        return ResponseEntity.ok(routes); // 200 ok + список маршрутов
    }

    @GetMapping("/allR")
    public ResponseEntity<List<Route>> getRoutes() {
        List<Route> routes = routeRepository.findAll();
        if (routes.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 no content
        }
        return ResponseEntity.ok(routes); // 200 ok + список точек
    }

    @GetMapping("/allP")
    public ResponseEntity<List<Point>> getPoints() {
        List<Point> points = pointRepository.findAll();
        if (points.isEmpty()) {
            return ResponseEntity.noContent().build(); // 204 no content
        }
        return ResponseEntity.ok(points); // 200 ok + список точек
    }

    @PostMapping
    public ResponseEntity<Route> addRoute(@RequestBody Route route) {
        Route savedRoute = routeService.addRoute(route);
        if (savedRoute == null)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // 400 bad request
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoute); // 201 created + маршрут
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<Route> updateRoute(@PathVariable String routeId, @RequestBody Route newRoute) {
        Route route = routeService.updateRoute(routeId, newRoute);
        if (route == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(route); // 200 ok + список точек
    }
}