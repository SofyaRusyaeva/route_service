package com.example.route_service.api.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteAnalyticsDto {

    String routeId;
    long totalStarts;
//    long totalCompletions;
//    long totalCancellations;

    Double completionsPercent;
    Double cancellationsPercent;

    Double avgRating;
    Double avgDuration;
    Double avgCoverage;
    Double avgOrder;

    Map<String, Long> missedPointsFrequency = new HashMap<>();
    Map<String, Long> extraPointsFrequency = new HashMap<>();
    Map<String, Long> outOfOrderPointsFrequency = new HashMap<>();

    Map<String, Double> avgPointDurations = new HashMap<>();

}
