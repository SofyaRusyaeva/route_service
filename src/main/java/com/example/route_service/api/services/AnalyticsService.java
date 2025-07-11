package com.example.route_service.api.services;

import com.example.route_service.api.dto.RouteAnalyticsDto;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.mappers.RouteMapper;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.PassageAnalytics;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AnalyticsService {

    RouteRepository routeRepository;
    PassageRepository passageRepository;
    AtomicUpdateService atomicUpdateService;
    RouteMapper routeMapper;

    public RouteAnalyticsDto getAnalytics(String routeId) {
        RouteDocument route = routeRepository.findById(routeId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Route %s not found", routeId))
        );

        return routeMapper.toAnalytics(route);
    }

    @Scheduled(cron = "${analytics.cron.expression}")
    @Transactional
    public void fullAnalysis() {
        log.info("Analysis is started");

        aggregatePassages();

        log.info("Analysis is finished");
    }

    public Set<String> aggregatePassages() {
        List<PassageDocument> updatedPassages = new ArrayList<>();
        Set<String> affectedRouteIds = new HashSet<>();

        try (Stream<PassageDocument> passagesToAnalyze = passageRepository.findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED)) {
            passagesToAnalyze.forEach(passage -> {
                try {
                    log.info("Analyzing passage with id: {}", passage.getPassageId());

                    RouteDocument route = routeRepository.findById(passage.getRouteId()).orElseThrow(
                            () -> new ObjectNotFoundException(String.format("Route %s not found", passage.getRouteId()))
                    );

                    PassageAnalytics passageAnalytics = integrityCheck(passage, route);
                    passage.setPassageAnalytics(passageAnalytics);

//                    updateRouteAnalytics(passage);
                    atomicUpdateService.aggregatePassageAnalytics(passage);

                    passage.setAnalyzed(true);
                    updatedPassages.add(passage);
                    affectedRouteIds.add(passage.getRouteId());
                    log.info("Successfully analyzed passage with id: {}", passage.getPassageId());

                } catch (ObjectNotFoundException e) {
                    log.error("Failed to analyze passage {}: {}. Marking as analyzed to avoid retries.", passage.getPassageId(), e.getMessage());
                    passage.setAnalyzed(true);
                    updatedPassages.add(passage);
                } catch (Exception e) {
                    log.error("An unexpected error occurred while analyzing passage {}:", passage.getPassageId(), e);
                }
            });
        }
        if (!updatedPassages.isEmpty()) {
            passageRepository.saveAll(updatedPassages);
        }
        return affectedRouteIds;
    }

    private PassageAnalytics integrityCheck(PassageDocument passage, RouteDocument route) {

//        RouteDocument route = routeRepository.findById(passage.getRouteId()).orElseThrow(
//                () -> new ObjectNotFoundException(String.format("Route %s not found", passage.getRouteId()))
//        );

        List<String> routePointIds = route.getPointsId();
        List<String> passagePointsIds = passage.getVisitedPoints().stream()
                .map(VisitedPoint::getPointId).toList();

        Set<String> routeSet = new HashSet<>(routePointIds);
        Set<String> passageSet = new HashSet<>(passagePointsIds);

        Set<String> missedPoints = new HashSet<>(routeSet);
        missedPoints.removeAll(passageSet);

        Set<String> extraPoints = new HashSet<>(passageSet);
        extraPoints.removeAll(routeSet);


        List<String> lcsSequence = calculateLCS(routePointIds, passagePointsIds);
        Set<String> lcsSet = new HashSet<>(lcsSequence);

        Set<String> common = new HashSet<>(routeSet);
        common.retainAll(passageSet);

        Set<String> outOfOrderPoints = new HashSet<>(common);
        outOfOrderPoints.removeAll(lcsSet);


        // TODO подумать над расчетом coverage
        double coverage = (double) (routeSet.size() - missedPoints.size()) / routeSet.size();
        double order = (double) lcsSequence.size() / routePointIds.size();

        return new PassageAnalytics(coverage, order, missedPoints.stream().toList(), extraPoints.stream().toList(), outOfOrderPoints.stream().toList());
    }

    private List<String> calculateLCS(List<String> routePointIds, List<String> passagePointIds) {
        int m = routePointIds.size();
        int n = passagePointIds.size();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else if (routePointIds.get(i - 1).equals(passagePointIds.get(j - 1))) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        List<String> lcsSequence = new ArrayList<>();

        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (routePointIds.get(i - 1).equals(passagePointIds.get(j - 1))) {
                lcsSequence.add(routePointIds.get(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        Collections.reverse(lcsSequence);
        return lcsSequence;
    }

}
