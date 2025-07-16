package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.dto.RouteRequest;
import com.example.route_service.api.dto.RouteResponse;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.LocationData;
import com.example.route_service.store.documents.models.RouteAnalytics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteMapperTest {

    private RouteMapper routeMapper;

    @BeforeEach
    void setUp() {
        routeMapper = new RouteMapperImpl();
    }

    @Test
    void toDocument() {
        HashMap<String, String> description = new HashMap<>();
        List<String> pointIds = List.of("point1", "point2", "point3");
        description.put("рейтинг", "4.85");
        RouteRequest request = new RouteRequest(pointIds, true, description);

        RouteDocument document = routeMapper.toDocument(request);

        assertNotNull(document);
        assertNull(document.getRouteId());
        assertNull(document.getUserId());
        assertNotNull(document.getRouteAnalytics());
        assertEquals(pointIds, document.getPointsId());
        assertEquals("4.85", document.getDescription().get("рейтинг"));
        assertTrue(document.isPublic());
    }

    @Test
    void toResponse() {
        HashMap<String, String> description = new HashMap<>();
        description.put("рейтинг", "4.85");
        RouteDocument document = new RouteDocument("route1", "user1",
                List.of("point1", "point2"), true, description, new RouteAnalytics());

        LocationData locationData = new LocationData("парк Гагарина", "Хорошее место", new HashMap<>());
        List<PointResponse> pointResponses = List.of(
                new PointResponse("point1", "парк", new GeoJsonPoint(50.199265, 53.227925), "промышленный район", locationData),
                new PointResponse("point2", "парк", new GeoJsonPoint(50.199265, 53.227925), "промышленный район", locationData)
        );

        RouteResponse response = routeMapper.toResponse(document, pointResponses);

        assertNotNull(response);
        assertNotNull(response.getPoints());
        assertEquals("route1", response.getRouteId());
        assertEquals("user1", response.getUserId());
        assertTrue(response.isPublic());
        assertEquals("4.85", response.getDescription().get("рейтинг"));
        assertEquals(2, response.getPoints().size());
        assertEquals("point1", response.getPoints().get(0).getPointId());
        assertEquals("point2", response.getPoints().get(1).getPointId());
    }

    @Test
    void toResponseWhenPointsListIsEmpty() {
        RouteDocument document = new RouteDocument();
        document.setRouteId("route1");
        List<PointResponse> emptyPointResponses = Collections.emptyList();

        RouteResponse response = routeMapper.toResponse(document, emptyPointResponses);

        assertNotNull(response);
        assertNotNull(response.getPoints());
        assertTrue(response.getPoints().isEmpty());
    }

    @Test
    void toDocumentWhenGivenNullRequest() {
        RouteRequest request = null;

        RouteDocument document = routeMapper.toDocument(request);

        assertNull(document);
    }

    @Test
    void toResponseWhenGivenNullDocument() {
        RouteDocument document = null;
        List<PointResponse> pointResponses = null;

        RouteResponse response = routeMapper.toResponse(document, pointResponses);

        assertNull(response);
    }

//    @Test
//    void toAnalyticsWhenAllDataIsPresent() {
//        RouteAnalytics analytics = new RouteAnalytics();
//        analytics.setTotalStarts(20L);
//        analytics.setTotalCompletions(10L);
//        analytics.setTotalCancellations(10L);
//        analytics.setTotalRating(20.0); // средний рейтинг 20/5 = 4.0
//        analytics.setRatingsCount(5L);
//        analytics.setTotalDuration(1000L); // средняя длительность 1000/10 = 100.0
//        analytics.setTotalCoverage(6.0); // среднее покрытие 6/8 = 0.75
//        analytics.setTotalOrder(7.5); // средний порядок 7.5/8 = 0.9375
//        analytics.setPassagesAnalyzedCount(8L);
//        analytics.setTotalPointDurations(Map.of("p1", 50L, "p2", 500L)); // среднее время на точке: p1=10.0, p2=50.0
//        analytics.setPointVisitCount(Map.of("p1", 5L, "p2", 10L));
//
//        RouteDocument document = new RouteDocument();
//        document.setRouteAnalytics(analytics);
//        document.setRouteId("route1");
//
//        RouteAnalyticsDto analyticsDto = routeMapper.toAnalytics(document);
//
//        assertNotNull(analyticsDto);
//        assertNotNull(analyticsDto.getAvgPointDurations());
//        assertEquals("route1", analyticsDto.getRouteId());
//        assertEquals(20L, analyticsDto.getTotalStarts());
//        assertEquals(0.5, analyticsDto.getCompletionsPercent());
//        assertEquals(0.5, analyticsDto.getCancellationsPercent());
//        assertEquals(4.0, analyticsDto.getAvgRating());
//        assertEquals(100.0, analyticsDto.getAvgDuration());
//        assertEquals(0.75, analyticsDto.getAvgCoverage());
//        assertEquals(0.9375, analyticsDto.getAvgOrder());
//        assertEquals(2, analyticsDto.getAvgPointDurations().size());
//        assertEquals(10.0, analyticsDto.getAvgPointDurations().get("p1"));
//        assertEquals(50.0, analyticsDto.getAvgPointDurations().get("p2"));
//    }
//
//    @Test
//    void toAnalyticsWhenZeroValues() {
//        RouteAnalytics analytics = new RouteAnalytics();
//        analytics.setTotalStarts(1L);
//        analytics.setTotalCompletions(0L);
//        analytics.setRatingsCount(0L);
//        analytics.setPassagesAnalyzedCount(0L);
//        analytics.setTotalRating(10.0);
//        analytics.setTotalDuration(1000L);
//
//        RouteDocument route = new RouteDocument();
//        route.setRouteAnalytics(analytics);
//
//        RouteAnalyticsDto dto = routeMapper.toAnalytics(route);
//
//        assertNotNull(dto);
//        assertNull(dto.getAvgRating());
//        assertNull(dto.getAvgDuration());
//        assertNull(dto.getAvgCoverage());
//        assertNull(dto.getAvgOrder());
//    }
//
//    @Test
//    void toAnalyticsWhenAnalyticsIsNull() {
//        RouteAnalytics analytics = null;
//        RouteDocument document = new RouteDocument();
//        document.setRouteAnalytics(analytics);
//
//        RouteAnalyticsDto analyticsDto = routeMapper.toAnalytics(document);
//
//        assertNull(analyticsDto);
//    }
//
//    @Test
//    void toAnalyticsWhenPointMapsAreEmpty() {
//        RouteAnalytics analytics = new RouteAnalytics();
//        analytics.setPointVisitCount(Collections.emptyMap());
//        analytics.setTotalPointDurations(Collections.emptyMap());
//
//        RouteDocument document = new RouteDocument();
//        document.setRouteAnalytics(analytics);
//
//        RouteAnalyticsDto dto2 = routeMapper.toAnalytics(document);
//        assertNotNull(dto2.getAvgPointDurations());
//        assertTrue(dto2.getAvgPointDurations().isEmpty());
//    }
}