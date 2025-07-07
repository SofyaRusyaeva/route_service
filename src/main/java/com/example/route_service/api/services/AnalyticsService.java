package com.example.route_service.api.services;

import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.Analysis;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AnalyticsService {

    RouteRepository routeRepository;

//    @Scheduled()
//    public void aggregate() {
//
//    }

    public Analysis integrityCheck(PassageDocument passage) {

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
        double coverage = (double) (routeSet.size() - missedPoints.size()) / passageSet.size();
        double order = (double) lcsSequence.size() / routePointIds.size();

        return new Analysis(coverage, order, missedPoints.stream().toList(), extraPoints.stream().toList(), outOfOrderPoints.stream().toList());
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
