package com.example.route_service.api.controllers;


import com.example.route_service.api.dto.RouteDto;
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

    @GetMapping()
    public ResponseEntity<List<RouteDocument>> getMyRoutes() {
        return ResponseEntity.ok(routeService.getRoutes());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<RouteDocument>> getUserRoutes(@PathVariable String userId) {
        return ResponseEntity.ok(routeService.getUserRoutes(userId));
    }

    @PostMapping
    public ResponseEntity<RouteDocument> createRoute(@Valid @RequestBody RouteDto route) {
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.addRoute(route));
    }

    @PatchMapping("/{routeId}/points")
    public ResponseEntity<RouteDocument> updateRoutePoints(@Valid @PathVariable String routeId, @Valid @RequestBody RouteDto newRoute) {
        return ResponseEntity.ok(routeService.updateRoute(routeId, newRoute));
    }

    @PatchMapping("/{routeId}/visibility")
    public ResponseEntity<RouteDocument> changeRouteVisibility(@PathVariable String routeId) {
        return ResponseEntity.ok(routeService.changeVisibility(routeId));
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable String routeId) {
        routeService.deleteRoute(routeId);
        return ResponseEntity.ok(String.format("Route %s deleted", routeId));
    }
}