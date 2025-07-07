package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.PassageRequest;
import com.example.route_service.api.dto.StartPassageResponse;
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
    public ResponseEntity<StartPassageResponse> startPassage(@PathVariable String routeId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(passageService.startPassage(routeId));
    }

    @PostMapping("/passage/{passageId}/finish")
    public ResponseEntity<Void> finishPassage(@PathVariable String passageId,
                                              @Valid @RequestBody PassageRequest request) {
        passageService.finishPassage(passageId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/passage/{passageId}/cancel")
    public ResponseEntity<Void> cancelPassage(@PathVariable String passageId,
                                              @Valid @RequestBody PassageRequest request) {

        passageService.cancelPassage(passageId, request);
        return ResponseEntity.ok().build();
    }

}
