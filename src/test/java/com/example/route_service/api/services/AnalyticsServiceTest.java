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
    void getAnalytics_whenRouteDataIsValid_shouldCalculateAndReturnDto() {
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
    void getAnalytics_whenRouteNotFound_shouldThrowException() {
        String routeId = "route1";
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(ObjectNotFoundException.class, () -> analyticsService.getAnalytics(routeId));
    }

    @Test
    void getAnalytics_whenCountersAreZero_shouldReturnNullForAverages() {
        RouteAnalytics rawAnalytics = new RouteAnalytics();
        rawAnalytics.setTotalStarts(0L);
        RouteDocument route = new RouteDocument();
        route.setRouteAnalytics(rawAnalytics);
        when(routeRepository.findById(anyString())).thenReturn(Optional.of(route));

        RouteAnalyticsDto result = analyticsService.getAnalytics("route1");

        assertNull(result.getCompletionsPercent());
        assertNull(result.getAvgRating());
    }

    @Test
    void getAnalytics_whenAnalyticsIsNull_shouldReturnNull() {
        String routeId = "route1";
        RouteDocument route = new RouteDocument();
        route.setRouteId(routeId);
        route.setRouteAnalytics(null);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));

        assertNull(analyticsService.getAnalytics(routeId));
    }

    @Test
    void fullAnalysis_whenDataIsValid() {
        RouteDocument route = new RouteDocument("r1", "u1", List.of("p1"), false, null, null);
        PassageDocument passage = new PassageDocument("pass1", "u1", "r1", PassageStatus.COMPLETED, null, null, new ArrayList<>(), null, null, false);

        when(passageRepository.findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED))
                .thenReturn(Stream.of(passage));
        when(routeRepository.findAllById(Set.of("r1"))).thenReturn(List.of(route));

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
    void fullAnalysis_whenNoPassages() {
        when(passageRepository.findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED))
                .thenReturn(Stream.empty()); // Возвращаем пустой список

        analyticsService.fullAnalysis();

        verify(routeRepository, never()).findAllById(any());
        verify(atomicUpdateService, never()).aggregatePassageAnalytics(any());
    }

    @Test
    void fullAnalysis_whenRouteIsMissing() {
        PassageDocument passageWithDeletedRoute = new PassageDocument("pass1", "u1", "deleted-r1", PassageStatus.COMPLETED, null, null, new ArrayList<>(), null, null, false);
        PassageDocument normalPassage = new PassageDocument("pass2", "u1", "r2", PassageStatus.COMPLETED, null, null, new ArrayList<>(), null, null, false);
        RouteDocument normalRoute = new RouteDocument("r2", null, List.of("p1"), false, null, null);

        when(passageRepository.findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED))
                .thenReturn(Stream.of(passageWithDeletedRoute, normalPassage));

        when(routeRepository.findAllById(Set.of("deleted-r1", "r2"))).thenReturn(List.of(normalRoute));


        analyticsService.fullAnalysis();

        verify(atomicUpdateService, times(1)).aggregatePassageAnalytics(normalPassage);
        verify(atomicUpdateService, never()).aggregatePassageAnalytics(passageWithDeletedRoute);

        ArgumentCaptor<List<PassageDocument>> passageListCaptor = ArgumentCaptor.forClass(List.class);
        verify(passageRepository).saveAll(passageListCaptor.capture());

        List<PassageDocument> savedPassages = passageListCaptor.getValue();
        assertEquals(2, savedPassages.size());
        assertTrue(savedPassages.stream().allMatch(PassageDocument::isAnalyzed));
    }

    @Test
    void integrityCheck_shouldCalculateCorrectly() {
        RouteDocument route = new RouteDocument(null, null, List.of("p1", "p2", "p2"), false, null, null);
        PassageDocument passage = new PassageDocument();
        passage.setVisitedPoints(List.of(new VisitedPoint("p1", null, null)));

        PassageAnalytics result = analyticsService.integrityCheck(passage, route);

        assertEquals(2, result.getMissedPoints().size()); // Пропущено два 'B'
        assertTrue(result.getMissedPoints().contains("p2"));
        // (3-2)/3 = 0.333
        assertEquals(1.0 / 3.0, result.getCoverage(), 0.001);
    }
}