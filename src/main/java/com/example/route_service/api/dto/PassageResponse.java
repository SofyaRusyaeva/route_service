package com.example.route_service.api.dto;

import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.enums.PassageStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassageResponse {

    String passageId;
    String userId;
    String routeId;
    PassageStatus status;
    Instant startTime;
    Instant endTime;
    List<VisitedPoint> visitedPoints;
    Feedback feedback;
}
