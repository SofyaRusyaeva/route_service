package com.example.route_service.api.controllers;


import com.example.route_service.api.services.AuthService;
import com.example.route_service.api.services.RouteService;
import com.example.route_service.store.documents.RouteDocument;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/routes")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteController {

    RouteService routeService;
    AuthService authService;

    @GetMapping()
    public ResponseEntity<List<RouteDocument>> getUserRoutes() {
        String userId = authService.getCurrentUserId();
        List<RouteDocument> routes = routeService.getRoutesByUserId(userId);
        return ResponseEntity.ok(routes);
    }

    @PostMapping
    public ResponseEntity<RouteDocument> addRoute(@Valid @RequestBody RouteDocument route) {
        RouteDocument savedRoute = routeService.addRoute(route);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoute);
    }


    @PutMapping("/{routeId}")
    public ResponseEntity<RouteDocument> updateRoute(@Valid @PathVariable String routeId, @Valid @RequestBody RouteDocument newRoute) {
        RouteDocument route = routeService.updateRoute(routeId, newRoute);
        return ResponseEntity.ok(route);
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable String routeId) {
        routeService.deleteRoute(routeId);
        return ResponseEntity.ok(String.format("Route %s deleted", routeId));
    }
}