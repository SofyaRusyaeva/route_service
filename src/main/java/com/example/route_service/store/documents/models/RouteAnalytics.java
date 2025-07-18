package com.example.route_service.store.documents.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteAnalytics {

    long totalStarts = 0;
    long totalCompletions = 0;
    long totalCancellations = 0;

    Double totalRating = 0.0;
    long ratingsCount = 0;

    long totalDuration = 0;

    Double totalCoverage = 0.0;
    Double totalOrder = 0.0;
//    Long passagesAnalyzedCount = 0L;

//    Double avgRating;
//    Double avgDuration;
//    Double avgCoverage;
//    Double avgOrder;

    Map<String, Long> missedPointsFrequency = new HashMap<>();
    Map<String, Long> extraPointsFrequency = new HashMap<>();
    Map<String, Long> outOfOrderPointsFrequency = new HashMap<>();

    Map<String, Long> totalPointDurations = new HashMap<>();
    Map<String, Long> pointVisitCount = new HashMap<>();

//    Map<String, Long> avgPointDurations = new HashMap<>();
}