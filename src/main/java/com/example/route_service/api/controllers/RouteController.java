package com.example.route_service.api.controllers;


import com.example.route_service.api.dto.RouteDto;
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
        return ResponseEntity.ok(routeService.getRoutesByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<RouteDocument> addRoute(@Valid @RequestBody RouteDto route) {
        String userId = authService.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.addRoute(route, userId));
    }


    @PutMapping("/{routeId}")
    public ResponseEntity<RouteDocument> updateRoute(@Valid @PathVariable String routeId, @Valid @RequestBody RouteDto newRoute) {
        return ResponseEntity.ok(routeService.updateRoute(routeId, newRoute));
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<?> deleteRoute(@PathVariable String routeId) {
        routeService.deleteRoute(routeId);
        return ResponseEntity.ok(String.format("Route %s deleted", routeId));
    }
}