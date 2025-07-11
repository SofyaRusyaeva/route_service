package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.RouteAnalyticsDto;
import com.example.route_service.api.services.AnalyticsService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/analytics")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyticsController {

    AnalyticsService analyticsService;

    @GetMapping("/{routeId}")
    public ResponseEntity<RouteAnalyticsDto> getAnalytics(@PathVariable String routeId) {
        return ResponseEntity.ok(analyticsService.getAnalytics(routeId));
    }
}
