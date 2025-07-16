package com.example.route_service.api.services;

import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AnalyticsServiceTest {

    @Mock
    RouteRepository routeRepository;
    @Mock
    PassageRepository passageRepository;
    @InjectMocks
    AnalyticsService analyticsService;

//    @Test
//    void Lcs() {
//        List<String> routePointIds = List.of("1", "2", "3", "5", "4", "5", "6", "7");
//        List<String> passagePointIds = List.of("1", "2", "2", "3", "4", "5", "6", "7");
//
//        List<String> expect = List.of("1", "2", "3", "4", "5", "6", "7");
//        List<String> lcs = analyticsService.calculateLCS(passagePointIds, routePointIds);
//
//        System.out.println(lcs);
//
//        assertEquals(expect.get(0), lcs.get(0));
//        assertEquals(expect.get(1), lcs.get(1));
//        assertEquals(expect.get(2), lcs.get(2));
//        assertEquals(expect.get(3), lcs.get(3));
//        assertEquals(expect.get(4), lcs.get(4));
//        assertEquals(expect.get(5), lcs.get(5));
//        assertEquals(expect.get(6), lcs.get(6));
//    }

//    @Test
//    void integrityCheck() {
//        PassageDocument passageDocument = new PassageDocument();
//        RouteDocument routeDocument = new RouteDocument();
//
//        List<VisitedPoint> visitedPoints = List.of(
//                new VisitedPoint("1", Instant.now(), Instant.now(), PointStatus.VISITED, null),
//                new VisitedPoint("2", Instant.now(), Instant.now(), PointStatus.VISITED, null),
//                new VisitedPoint("3", Instant.now(), Instant.now(), PointStatus.VISITED, null),
//                new VisitedPoint("4", Instant.now(), Instant.now(), PointStatus.VISITED, null),
//                new VisitedPoint("4", Instant.now(), Instant.now(), PointStatus.VISITED, null),
//                new VisitedPoint("5", Instant.now(), Instant.now(), PointStatus.VISITED, null),
//                new VisitedPoint("6", Instant.now(), Instant.now(), PointStatus.VISITED, null),
//                new VisitedPoint("7", Instant.now(), Instant.now(), PointStatus.VISITED, null)
//        );
//        List<String> points = List.of("1", "2", "2", "3", "4", "5", "6", "7");
//
//        passageDocument.setVisitedPoints(visitedPoints);
//        routeDocument.setPointsId(points);
//
//        PassageAnalytics analytics = analyticsService.integrityCheck(passageDocument, routeDocument);
//
//        assertEquals(1, analytics.getCoverage());
//        assertEquals(1, analytics.getOrder());
//        assertEquals(1, analytics.getExtraPoints().size());
//        assertEquals(1, analytics.getMissedPoints().size());
//    }

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
}