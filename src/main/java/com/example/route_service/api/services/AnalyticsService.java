package com.example.route_service.api.services;

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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AnalyticsService {

    RouteRepository routeRepository;
    PassageRepository passageRepository;

    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void fullAnalysis() {
        log.info("Analysis is started");

        Set<String> updatedRouteIds = aggregatePassage();

        if (updatedRouteIds.isEmpty()) {
            return;
        }

        for (String routeId : updatedRouteIds) {
            updateRoute(routeId);
        }

        log.info("Analysis is finished");
    }

    private void updateRoute(String routeId) {

        RouteDocument route = routeRepository.findById(routeId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Route %s not found", routeId))
        );

        List<PassageDocument> passages = passageRepository.findAllByRouteIdAndStatus(routeId, PassageStatus.COMPLETED);
        if (passages.isEmpty()) {
            return;
        }

        double totalRating = 0;
        int ratingsCount = 0;

        long totalDuration = 0;

        double totalCoverage = 0;
        double totalOrder = 0;

        int analysisCount = 0;

        Map<String, Long> missedPointFrequency = new HashMap<>();
        Map<String, Long> extraPointFrequency = new HashMap<>();
        Map<String, Long> outOfOrderPointFrequency = new HashMap<>();

        Map<String, Long> totalPointDurations = new HashMap<>();
        Map<String, Long> pointVisitCount = new HashMap<>();

        for (PassageDocument passage : passages) {
            if (passage.getFeedback() != null && passage.getFeedback().getRating() != null) {
                totalRating += passage.getFeedback().getRating();
                ratingsCount++;
            }

            if (passage.getStartTime() != null && passage.getEndTime() != null) {
                totalDuration += Duration.between(passage.getStartTime(), passage.getEndTime()).toSeconds();
            }

            PassageAnalytics analysis = passage.getPassageAnalytics();
            if (analysis != null) {
                totalCoverage += analysis.getCoverage();
                totalOrder += analysis.getOrder();
                analysisCount++;

                analysis.getMissedPoints().forEach(pointId -> missedPointFrequency.merge(pointId, 1L, Long::sum));
                analysis.getExtraPoints().forEach(pointId -> extraPointFrequency.merge(pointId, 1L, Long::sum));
                analysis.getOutOfOrderPoints().forEach(pointId -> outOfOrderPointFrequency.merge(pointId, 1L, Long::sum));
            }

            for (VisitedPoint point : passage.getVisitedPoints()) {
                if (point.getPointId() != null && point.getEntryTime() != null && point.getExitTime() != null) {
                    long durationOnPoint = Duration.between(point.getEntryTime(), point.getExitTime()).toSeconds();
                    totalPointDurations.merge(point.getPointId(), durationOnPoint, Long::sum);
                    pointVisitCount.merge(point.getPointId(), 1L, Long::sum);
                }
            }
        }
        RouteAnalytics analytics = route.getRouteAnalytics();
        if (analytics == null) {
            analytics = new RouteAnalytics();
            route.setRouteAnalytics(analytics);
        }

        long totalStarts = passageRepository.countByRouteId(routeId);
        long totalCancellations = passageRepository.countByRouteIdAndStatus(routeId, PassageStatus.CANCELLED);

        Map<String, Long> avgPointDurations = new HashMap<>();
        for (String pointId : pointVisitCount.keySet()) {
            long total = totalPointDurations.get(pointId);
            long count = totalPointDurations.get(pointId);
            if (count > 0) {
                avgPointDurations.put(pointId, total / count);
            }
        }

        analytics.setTotalCompletions(passages.size());
        analytics.setAvgRating(ratingsCount > 0 ? totalRating / ratingsCount : null);
        analytics.setAvgDuration(!passages.isEmpty() ? (double) totalDuration / passages.size() : null);
        analytics.setAvgCoverage(analysisCount > 0 ? totalCoverage / analysisCount : null);
        analytics.setAvgOrder(analysisCount > 0 ? totalOrder / analysisCount : null);
        analytics.setMissedPointsFrequency(missedPointFrequency);
        analytics.setExtraPointsFrequency(extraPointFrequency);
        analytics.setOutOfOrderPointsFrequency(outOfOrderPointFrequency);
        analytics.setTotalStarts(totalStarts);
        analytics.setTotalCancellations(totalCancellations);
        analytics.setAveragePointDurations(avgPointDurations);

        routeRepository.save(route);

    }

    public Set<String> aggregatePassage() {
        Set<String> passedRouteIds = new HashSet<>();
        try (Stream<PassageDocument> passagesToAnalyze = passageRepository.findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED)) {
            passagesToAnalyze.forEach(passage -> {
                try {
                    log.info("Analyzing passage with id: {}", passage.getPassageId());

                    PassageAnalytics passageAnalytics = integrityCheck(passage);
                    passage.setPassageAnalytics(passageAnalytics);
                    passage.setAnalyzed(true);
                    passageRepository.save(passage);
                    passedRouteIds.add(passage.getRouteId());

                    log.info("Successfully analyzed and saved passage with id: {}", passage.getPassageId());

                } catch (ObjectNotFoundException e) {
                    log.error("Failed to analyze passage {}: {}. Marking as analyzed to avoid retries.", passage.getPassageId(), e.getMessage());
                    passage.setAnalyzed(true);
                    passageRepository.save(passage);
                } catch (Exception e) {
                    log.error("An unexpected error occurred while analyzing passage {}:", passage.getPassageId(), e);
                }
            });
        }
        return passedRouteIds;
    }


    private PassageAnalytics integrityCheck(PassageDocument passage) {

        RouteDocument route = routeRepository.findById(passage.getRouteId()).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Route %s not found", passage.getRouteId()))
        );

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
