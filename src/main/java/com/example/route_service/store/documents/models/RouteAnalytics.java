package com.example.route_service.store.documents.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteAnalytics {

    long totalStarts = 0;
    long totalCompletions = 0;
    long totalCancellations = 0;

    Double avgRating;
    Double avgDuration;

    Double avgCoverage;
    Double avgOrder;

    Map<String, Long> missedPointsFrequency;
    Map<String, Long> extraPointsFrequency;
    Map<String, Long> outOfOrderPointsFrequency;

}
