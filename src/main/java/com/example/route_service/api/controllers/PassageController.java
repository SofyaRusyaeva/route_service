package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.PassageFeedbackRequest;
import com.example.route_service.api.dto.PassageResponse;
import com.example.route_service.api.services.PassageService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PassageController {

    PassageService passageService;

    @PostMapping("routes/{routeId}/start")
    public ResponseEntity<PassageResponse> startPassage(@PathVariable String routeId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passageService.startPassage(routeId));
    }

    @PostMapping("/passage/{passageId}/finish")
    public ResponseEntity<PassageResponse> finishPassage(@PathVariable String passageId,
                                                         @Valid @RequestBody PassageFeedbackRequest request) {
        return ResponseEntity.ok(passageService.finishPassage(passageId, request));
    }

    @PostMapping("/passage/{passageId}/cancel")
    public ResponseEntity<PassageResponse> cancelPassage(@PathVariable String passageId) {

        return ResponseEntity.ok(passageService.cancelPassage(passageId));
    }

}
