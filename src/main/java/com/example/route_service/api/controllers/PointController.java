package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.PointRequest;
import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.services.PointService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PointController {

    PointService pointService;

    @GetMapping("/{pointId}")
    public ResponseEntity<PointResponse> getPointById(@PathVariable String pointId) {
        return ResponseEntity.ok(pointService.getPointById(pointId));
    }

    @PutMapping("/{pointId}")
    public ResponseEntity<PointResponse> updatePoint(@PathVariable String pointId, @Valid @RequestBody PointRequest point) {
        return ResponseEntity.ok(pointService.updatePoint(pointId, point));
    }

    @PostMapping
    public ResponseEntity<PointResponse> addPoint(@Valid @RequestBody PointRequest point) {
        return ResponseEntity.ok(pointService.addPoint(point));
    }

    @DeleteMapping("/{pointId}")
    public ResponseEntity<Void> deletePoint(@PathVariable String pointId) {
        pointService.deletePoint(pointId);
        return ResponseEntity.noContent().build();
    }
}
