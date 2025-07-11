package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.dto.RouteAnalyticsDto;
import com.example.route_service.api.dto.RouteRequest;
import com.example.route_service.api.dto.RouteResponse;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.RouteAnalytics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    @Mapping(target = "routeId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "routeAnalytics", ignore = true)
    RouteDocument toDocument(RouteRequest request);

    @Mapping(source = "routeDocument.routeId", target = "routeId")
    @Mapping(source = "routeDocument.userId", target = "userId")
//    @Mapping(source = "routeDocument.isPublic", target = "isPublic")
    @Mapping(target = "isPublic", expression = "java(routeDocument.isPublic())")
    @Mapping(source = "routeDocument.description", target = "description")
    @Mapping(source = "points", target = "points")
    RouteResponse toResponse(RouteDocument routeDocument, List<PointResponse> points);


    default RouteAnalyticsDto toAnalytics(RouteDocument route) {
        RouteAnalytics analytics = route.getRouteAnalytics();

        if (analytics == null) {
            return null;
        }
        Double avgRating = avg(analytics.getTotalRating(), analytics.getRatingsCount());
        Double avgOrder = avg(analytics.getTotalOrder(), analytics.getPassagesAnalyzedCount());
        Double avgCoverage = avg(analytics.getTotalCoverage(), analytics.getPassagesAnalyzedCount());
        Double avgDuration = avg(analytics.getTotalDuration(), analytics.getTotalCompletions());

        Map<String, Double> avgPointDurations = avgPointDurations(analytics.getTotalPointDurations(), analytics.getPointVisitCount());

        return new RouteAnalyticsDto(
                route.getRouteId(),
                analytics.getTotalStarts(),
                analytics.getTotalCompletions(),
                avgRating,
                avgDuration,
                avgCoverage,
                avgOrder,
                analytics.getMissedPointsFrequency(),
                analytics.getExtraPointsFrequency(),
                analytics.getOutOfOrderPointsFrequency(),
                avgPointDurations
        );
    }

    private Map<String, Double> avgPointDurations(Map<String, Long> totalPointDurations, Map<String, Long> pointVisitCount) {
        if (pointVisitCount == null || pointVisitCount.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Double> resultMap = new HashMap<>();

        pointVisitCount.forEach((pointId, count) -> {
            long totalDuration = totalPointDurations.getOrDefault(pointId, 0L);
            resultMap.put(pointId, (double) totalDuration / count);
        });

        return resultMap;
    }

    private Double avg(Number total, Long count) {
        if (total == null || count == null || count == 0) {
            return null;
        }
        return total.doubleValue() / count;
    }
}
