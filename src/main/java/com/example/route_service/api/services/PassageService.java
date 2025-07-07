package com.example.route_service.api.services;

import com.example.route_service.api.dto.PassageRequest;
import com.example.route_service.api.dto.StartPassageResponse;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.StateException;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.Analysis;
import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO подумать над завершениями: два одинаковых метода

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PassageService {

    PassageRepository passageRepository;
    RouteRepository routeRepository;
    AuthService authService;

    @Transactional
    public StartPassageResponse startPassage(String routeId) {

        String userId = authService.getCurrentUserId();

        routeRepository.findById(routeId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Route %s not found", routeId)));
        PassageDocument passage = new PassageDocument();

        passage.setUserId(userId);
        passage.setRouteId(routeId);
        passage.setPassageStatus(PassageStatus.IN_PROGRESS);
        passage.setStartTime(Instant.now());

        passageRepository.save(passage);
        return new StartPassageResponse(passage.getPassageId());
    }

    // TODO подумать над возвращаемыми значениями
    @Transactional
    public void finishPassage(String passageId, PassageRequest request) {

        String userId = authService.getCurrentUserId();

        PassageDocument passage = findAndValidateRoute(userId, passageId);

        passage.setEndTime(Instant.now());
        passage.setFeedback(new Feedback(request.getRating(), request.getComment()));
        passage.setPassageStatus(PassageStatus.COMPLETED);

        Analysis analysis = integrityCheck(passage);
        passage.setAnalysis(analysis);

        passageRepository.save(passage);
    }

    // TODO подумать над возвращаемыми значениями
    @Transactional
    public void cancelPassage(String passageId, PassageRequest request) {

        String userId = authService.getCurrentUserId();

        PassageDocument passage = findAndValidateRoute(userId, passageId);

        passage.setEndTime(Instant.now());
        passage.setFeedback(new Feedback(request.getRating(), request.getComment()));
        passage.setPassageStatus(PassageStatus.CANCELLED);

        passageRepository.save(passage);
    }


    private PassageDocument findAndValidateRoute(String userId, String passageId) {
        PassageDocument passage = passageRepository.findById(passageId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Passage %s not found", passageId))
        );

        if (passage.getPassageStatus() != PassageStatus.IN_PROGRESS)
            throw new StateException("Passage is already finished or canceled");

        // TODO исправить исключение
        if (!passage.getUserId().equals(userId))
            throw new RuntimeException("user can't modify this passage");

        return passage;
    }

    private Analysis integrityCheck(PassageDocument passage) {

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

        // TODO подумать над расчетом coverage
        double coverage = (double) (routeSet.size() - missedPoints.size()) / passageSet.size();
        double order = (double) calculateLCS(routePointIds, passagePointsIds) / routePointIds.size();

        return new Analysis(coverage, order, missedPoints.stream().toList(), extraPoints.stream().toList());
    }

    private int calculateLCS(List<String> routePointIds, List<String> passagePointIds) {
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
        return dp[m][n];
    }
}
