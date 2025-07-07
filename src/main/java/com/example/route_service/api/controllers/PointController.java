package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.PointDto;
import com.example.route_service.api.services.PointService;
import com.example.route_service.store.documents.PointDocument;
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
    public ResponseEntity<PointDocument> getPointById(@PathVariable String pointId) {
        return ResponseEntity.ok(pointService.getPointById(pointId));
    }

    @PutMapping("/{pointId}")
    public ResponseEntity<PointDocument> updatePoint(@PathVariable String pointId, @Valid @RequestBody PointDto point) {
        return ResponseEntity.ok(pointService.updatePoint(pointId, point));
    }

    @PostMapping
    public ResponseEntity<PointDocument> addPoint(@Valid @RequestBody PointDto point) {
        return ResponseEntity.ok(pointService.addPoint(point));
    }

    @DeleteMapping("/{pointId}")
    public ResponseEntity<Void> deletePoint(@PathVariable String pointId) {
        pointService.deletePoint(pointId);
        return ResponseEntity.noContent().build();
    }
}
