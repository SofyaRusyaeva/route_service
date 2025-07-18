package com.example.route_service.api.services;

import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.documents.models.PassageAnalytics;
import com.example.route_service.store.documents.models.RouteAnalytics;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@Slf4j
public class AnalyticsServiceIntegrationTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:6.0");
    @Autowired
    private AnalyticsService analyticsService;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private PassageRepository passageRepository;
    @Autowired
    private AtomicUpdateService atomicUpdateService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.task.scheduling.enabled", () -> "false");
    }

    @AfterEach
    void tearDown() {
        routeRepository.deleteAll();
        passageRepository.deleteAll();
    }

    @Test
    void fullAnalysis_withMultipleAndVariedPassages_shouldAggregateCorrectly() {
        RouteAnalytics routeAnalytics = new RouteAnalytics();
        routeAnalytics.setTotalStarts(5L);
        routeAnalytics.setTotalCompletions(1L);
        routeAnalytics.setRatingsCount(1L);
        routeAnalytics.setTotalRating(1.0);
        routeAnalytics.setTotalDuration(1000);
        routeAnalytics.setTotalCoverage(1.0);
        routeAnalytics.setTotalOrder(1.0);
        routeAnalytics.getTotalPointDurations().put("p1", 1000L);
        routeAnalytics.getTotalPointDurations().put("p2", 1000L);
        routeAnalytics.getPointVisitCount().put("p1", 1L);
        routeAnalytics.getPointVisitCount().put("p2", 1L);
        RouteDocument route = new RouteDocument("route1", "user1", List.of("p1", "p2"), false, new HashMap<>(), routeAnalytics);
        routeRepository.save(route);

        PassageDocument passage1 = new PassageDocument();
        passage1.setPassageId("pass1");
        passage1.setRouteId("route1");
        passage1.setStatus(PassageStatus.COMPLETED);
        passage1.setStartTime(Instant.now());
        passage1.setEndTime(Instant.now().plusSeconds(2000));
        passage1.setFeedback(new Feedback(5, ""));
        passage1.setVisitedPoints(List.of(new VisitedPoint("p1", Instant.now(), Instant.now().plusSeconds(1000), null, null)));

        PassageDocument passage2 = new PassageDocument();
        passage2.setPassageId("pass2");
        passage2.setRouteId("route1");
        passage2.setStartTime(Instant.now());
        passage2.setEndTime(Instant.now().plusSeconds(1000));
        passage2.setStatus(PassageStatus.COMPLETED);
        passage2.setFeedback(new Feedback(1, ""));
        passage2.setVisitedPoints(List.of(
                new VisitedPoint("p1", Instant.now(), Instant.now().plusSeconds(1000), null, null),
                new VisitedPoint("p2", Instant.now(), Instant.now().plusSeconds(1000), null, null),
                new VisitedPoint("p3", Instant.now(), Instant.now().plusSeconds(1000), null, null)
        ));

        PassageDocument passage3 = new PassageDocument();
        passage3.setPassageId("pass3");
        passage3.setRouteId("route1");
        passage3.setStatus(PassageStatus.CANCELLED);
        passage3.setFeedback(new Feedback(2, ""));

        PassageDocument passage4 = new PassageDocument();
        passage4.setPassageId("pass4");
        passage4.setRouteId("route1");
        passage4.setStatus(PassageStatus.COMPLETED);
        passage4.setAnalyzed(true);
        passage4.setFeedback(new Feedback(1, ""));
        passage4.setVisitedPoints(List.of(
                new VisitedPoint("p1", Instant.now(), Instant.now().plusSeconds(1000), null, null),
                new VisitedPoint("p2", Instant.now(), Instant.now().plusSeconds(1000), null, null)
        ));

        PassageDocument passage5 = new PassageDocument();
        passage5.setPassageId("pass5");
        passage5.setRouteId("route1");
        passage5.setStatus(PassageStatus.COMPLETED);
        passage5.setFeedback(new Feedback(1, ""));
        passage5.setStartTime(Instant.now());
        passage5.setEndTime(Instant.now().plusSeconds(4000));
        passage5.setVisitedPoints(List.of(
                new VisitedPoint("p2", Instant.now(), Instant.now().plusSeconds(1000), null, null),
                new VisitedPoint("p1", Instant.now(), Instant.now().plusSeconds(1000), null, null)
        ));
        passageRepository.saveAll(List.of(passage1, passage2, passage3, passage4, passage5));

        atomicUpdateService.incCancellations("route1");
        analyticsService.fullAnalysis();

        RouteDocument updatedRoute = routeRepository.findById("route1").orElseThrow();
        RouteAnalytics analytics = updatedRoute.getRouteAnalytics();

        assertNotNull(analytics);
        assertEquals(4, analytics.getTotalCompletions());
        assertEquals(1, analytics.getTotalCancellations());
        assertEquals(4, analytics.getRatingsCount());
        assertEquals(8.0, analytics.getTotalRating());
        assertEquals(8000, analytics.getTotalDuration());

        assertEquals(3.5, analytics.getTotalCoverage(), 0.001);
        assertEquals(3.0, analytics.getTotalOrder(), 0.001);

        assertEquals(1, analytics.getExtraPointsFrequency().get("p3"));
        assertNotNull(analytics.getMissedPointsFrequency());
        assertEquals(1L, analytics.getMissedPointsFrequency().getOrDefault("p2", 0L));
        assertNull(analytics.getMissedPointsFrequency().get("p1"));
        assertEquals(1L, analytics.getOutOfOrderPointsFrequency().get("p1"));
        assertNull(analytics.getOutOfOrderPointsFrequency().get("p2"));
        assertEquals(4000, analytics.getTotalPointDurations().get("p1"));
        assertEquals(3000, analytics.getTotalPointDurations().get("p2"));
        assertEquals(4, analytics.getPointVisitCount().get("p1"));
        assertEquals(3, analytics.getPointVisitCount().get("p2"));
    }

    @Test
    void fullAnalysisWhenEmptyRoute() {
        RouteDocument emptyRoute = new RouteDocument("route1", "user1", Collections.emptyList(), false, null, new RouteAnalytics());
        routeRepository.save(emptyRoute);

        PassageDocument passage = new PassageDocument();
        passage.setPassageId("pass1");
        passage.setRouteId("route1");
        passage.setStatus(PassageStatus.COMPLETED);
        passage.setAnalyzed(false);
        passage.setVisitedPoints(Collections.emptyList());

        passageRepository.save(passage);

        assertDoesNotThrow(() -> {
            analyticsService.fullAnalysis();
        });

        RouteDocument updatedRoute = routeRepository.findById("route1").orElseThrow();
        RouteAnalytics analytics = updatedRoute.getRouteAnalytics();

        assertNotNull(analytics);
        assertEquals(1, analytics.getTotalCompletions());

        PassageDocument analyzedPassage = passageRepository.findById("pass1").orElseThrow();
        PassageAnalytics passageAnalytics = analyzedPassage.getPassageAnalytics();
        assertNotNull(passageAnalytics);
        assertEquals(0.0, passageAnalytics.getCoverage());
        assertEquals(0.0, passageAnalytics.getOrder());
    }
}
