package com.example.route_service.api.services;

import com.example.route_service.api.dto.RouteAnalyticsDto;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.PassageAnalytics;
import com.example.route_service.store.documents.models.RouteAnalytics;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class AnalyticsServiceTest {

    @Mock
    RouteRepository routeRepository;
    @Mock
    PassageRepository passageRepository;
    @Mock
    AtomicUpdateService atomicUpdateService;
    @InjectMocks
    AnalyticsService analyticsService;

    @Test
    void getAnalytics() {
        RouteDocument route = new RouteDocument();
        route.setRouteId("route1");

        Map<String, Long> visitCount = new HashMap<>();
        visitCount.put("p1", 2L);
        visitCount.put("p2", 1L);

        Map<String, Long> totalDurations = new HashMap<>();
        totalDurations.put("p1", 120L);
        totalDurations.put("p2", 300L);

        RouteAnalytics analytics = new RouteAnalytics(10L, 5L, 2L, 15.0, 4L, 3000L, 4.0, 4.75, null, null, null, totalDurations, visitCount);
        route.setRouteAnalytics(analytics);
        when(routeRepository.findById("route1")).thenReturn(Optional.of(route));

        RouteAnalyticsDto analyticsDto = analyticsService.getAnalytics("route1");

        assertNotNull(analyticsDto);
        assertEquals(10L, analyticsDto.getTotalStarts());
        assertEquals(0.5, analyticsDto.getCompletionsPercent());
        assertEquals(0.2, analyticsDto.getCancellationsPercent());
        assertEquals(3.75, analyticsDto.getAvgRating());
        assertEquals(600.0, analyticsDto.getAvgDuration());
        assertEquals(0.8, analyticsDto.getAvgCoverage());
        assertEquals(0.95, analyticsDto.getAvgOrder());
        Map<String, Double> avgPointDurations = analyticsDto.getAvgPointDurations();
        assertNotNull(avgPointDurations);
        assertEquals(2, avgPointDurations.size());
        assertEquals(60.0, avgPointDurations.get("p1"));
        assertEquals(300.0, avgPointDurations.get("p2"));
    }


    @Test
    void getAnalyticsWhenRouteNotFound() {
        String routeId = "route1";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> analyticsService.getAnalytics(routeId));
    }

    @Test
    void getAnalyticsWhenAnalyticsIsNull() {
        String routeId = "route1";
        RouteDocument route = new RouteDocument();
        route.setRouteId(routeId);
        route.setRouteAnalytics(null);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        assertNull(analyticsService.getAnalytics(routeId));
    }

    @Test
    void fullAnalysis() {
        RouteDocument route = new RouteDocument("route1", "user1", List.of("p1"), false, null, null);
        PassageDocument passage = new PassageDocument("passage1", "user1", "route1", PassageStatus.COMPLETED, null, null, new ArrayList<>(), null, null, false);

        when(passageRepository.findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED))
                .thenReturn(Stream.of(passage));
        when(routeRepository.findAllById(Set.of("route1"))).thenReturn(List.of(route));

        analyticsService.fullAnalysis();

        verify(atomicUpdateService, times(1)).aggregatePassageAnalytics(passage);

        ArgumentCaptor<List<PassageDocument>> passageListCaptor = ArgumentCaptor.forClass(List.class);
        verify(passageRepository).saveAll(passageListCaptor.capture());

        List<PassageDocument> savedPassages = passageListCaptor.getValue();
        assertEquals(1, savedPassages.size());
        assertTrue(savedPassages.get(0).isAnalyzed());
        assertNotNull(savedPassages.get(0).getPassageAnalytics());
    }

    @Test
    void fullAnalysisWhenNoPassages() {
        when(passageRepository.findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED))
                .thenReturn(Stream.empty()); // Возвращаем пустой список

        analyticsService.fullAnalysis();

        verify(routeRepository, never()).findAllById(any());
        verify(atomicUpdateService, never()).aggregatePassageAnalytics(any());
    }

    @Test
    void fullAnalysisWhenRouteIsMissing() {
        // Arrange
        PassageDocument passageWithDeletedRoute = new PassageDocument("p1", "u1", "deleted-r1", PassageStatus.COMPLETED, null, null, new ArrayList<>(), null, null, false);
        PassageDocument normalPassage = new PassageDocument("p2", "u1", "r2", PassageStatus.COMPLETED, null, null, new ArrayList<>(), null, null, false);
        RouteDocument normalRoute = new RouteDocument("r2", null, List.of("A"), false, null, null);

        when(passageRepository.findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED))
                .thenReturn(Stream.of(passageWithDeletedRoute, normalPassage));

        // Имитируем, что для 'deleted-r1' маршрут не нашелся
        when(routeRepository.findAllById(Set.of("deleted-r1", "r2"))).thenReturn(List.of(normalRoute));

        // Act
        analyticsService.fullAnalysis();

        // Assert
        // Проверяем, что апдейтер был вызван только для нормального прохождения
        verify(atomicUpdateService, times(1)).aggregatePassageAnalytics(normalPassage);
        verify(atomicUpdateService, never()).aggregatePassageAnalytics(passageWithDeletedRoute);

        // Проверяем, что ОБА прохождения были помечены как проанализированные
        ArgumentCaptor<List<PassageDocument>> passageListCaptor = ArgumentCaptor.forClass(List.class);
        verify(passageRepository).saveAll(passageListCaptor.capture());

        List<PassageDocument> savedPassages = passageListCaptor.getValue();
        assertEquals(2, savedPassages.size());
        // Проверяем, что оба флага isAnalyzed установлены в true
        assertTrue(savedPassages.stream().allMatch(PassageDocument::isAnalyzed));
    }

    // Тест для самого сложного приватного метода integrityCheck можно оставить как есть,
    // так как мы его уже тестировали отдельно.
    // Но для полноты картины, можно добавить и его.
    @Test
    void integrityCheck_shouldCalculateCorrectly() {
        RouteDocument route = new RouteDocument(null, null, List.of("A", "B", "B"), false, null, null);
        PassageDocument passage = new PassageDocument();
        passage.setVisitedPoints(List.of(new VisitedPoint("A", null, null, null, null)));

        PassageAnalytics result = analyticsService.integrityCheck(passage, route);

        assertEquals(2, result.getMissedPoints().size()); // Пропущено два 'B'
        assertTrue(result.getMissedPoints().contains("B"));
        // Покрытие (3-2)/3 ~ 0.333
        assertEquals(1.0 / 3.0, result.getCoverage(), 0.001);
    }
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

}