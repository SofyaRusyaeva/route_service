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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Сервис, отвечающий за обработку аналитических данных по маршрутам
 * Выполняет расчет и предоставление аналитики по запросу
 * Производит периодическую фоновую обработку сырых данных
 * Выполняет анализ целостности прохождения маршрута (соответствие порядка посещенных точек,
 * пропущенные и лишние точки)
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AnalyticsService {

    RouteRepository routeRepository;
    PassageRepository passageRepository;
    AtomicUpdateService atomicUpdateService;

    /**
     * Формирует DTO с рассчитанной аналитикой для конкретного маршрута
     * Извлекает агрегированные данные из {@link RouteAnalytics}
     * и вычисляет на их основе средние значения и проценты
     *
     * @param routeId Уникальный идентификатор маршрута
     * @return Объект {@link RouteAnalyticsDto} с готовыми метриками или `null`, если аналитика для маршрута отсутствует
     * @throws ObjectNotFoundException если маршрут с указанным id не найден
     */
    public RouteAnalyticsDto getAnalytics(String routeId) {
        RouteDocument route = routeRepository.findById(routeId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Route %s not found", routeId))
        );

        RouteAnalytics analytics = route.getRouteAnalytics();

        if (analytics == null) {
            return null;
        }
        Double avgRating = avg(analytics.getTotalRating(), analytics.getRatingsCount());
        Double avgOrder = avg(analytics.getTotalOrder(), analytics.getTotalCompletions()); // analytics.getPassagesAnalyzedCount()
        Double avgCoverage = avg(analytics.getTotalCoverage(), analytics.getTotalCompletions()); // analytics.getPassagesAnalyzedCount()
        Double avgDuration = avg(analytics.getTotalDuration(), analytics.getTotalCompletions());
        Double completionsPercent = avg(analytics.getTotalCompletions(), analytics.getTotalStarts());
        Double cancellationPercent = avg(analytics.getTotalCancellations(), analytics.getTotalStarts());

        Map<String, Double> avgPointDurations = avgPointDurations(analytics.getTotalPointDurations(), analytics.getPointVisitCount());

        return new RouteAnalyticsDto(
                route.getRouteId(),
                analytics.getTotalStarts(),
//                analytics.getTotalCompletions(),
//                analytics.getTotalCancellations(),
                completionsPercent,
                cancellationPercent,
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

    /**
     * Метод, запускаемый по расписанию для выполнения полного цикла анализа
     * Метод выполняется автоматически согласно выражению, указанному в `analytics.cron.expression`.
     * Делегирует основную работу методу {@link #aggregatePassages} и логирует
     * начало и конец процесса обработки
     */
    @Scheduled(cron = "${analytics.cron.expression}")
    @Transactional
    public void fullAnalysis() {
        log.info("Analysis is started");

        aggregatePassages();

        log.info("Analysis is finished");
    }

    /**
     * Выполняет основную логику фоновой обработки: находит все завершенные, но еще
     * не проанализированные прохождения, вычисляет для каждого из них аналитику,
     * атомарно обновляет общие данные по маршруту и помечает прохождение как обработанное.
     * В случае ошибки при анализе прохождения,
     * оно помечается как обработанное во избежание повторных сбоев
     */
    public void aggregatePassages() {
        final int BATCH_SIZE = 100;
        List<PassageDocument> batchToUpdate = new ArrayList<>(BATCH_SIZE);

        List<PassageDocument> passagesToAnalyze = passageRepository
                .findAllByStatusAndIsAnalyzedIsFalse(PassageStatus.COMPLETED).toList();
        if (passagesToAnalyze.isEmpty()) {
            log.info("No new passages to analyze.");
            return;
        }

        Set<String> routeIds = passagesToAnalyze.stream()
                .map(PassageDocument::getRouteId)
                .collect(Collectors.toSet());

        Map<String, RouteDocument> routeCache = routeRepository.findAllById(routeIds).stream()
                .collect(Collectors.toMap(RouteDocument::getRouteId, Function.identity()));

        for (PassageDocument passage : passagesToAnalyze) {
            try {
                log.info("Analyzing passage with id: {}", passage.getPassageId());

                RouteDocument route = routeCache.get(passage.getRouteId());
                if (route == null) {
                    throw new ObjectNotFoundException(String.format("Route %s in passage %s, was not found.",
                            passage.getRouteId(), passage.getPassageId()));
                }

                PassageAnalytics passageAnalytics = integrityCheck(passage, route);
                passage.setPassageAnalytics(passageAnalytics);

                atomicUpdateService.aggregatePassageAnalytics(passage);

                passage.setAnalyzed(true);
                batchToUpdate.add(passage);
                if (batchToUpdate.size() >= BATCH_SIZE) {
                    passageRepository.saveAll(batchToUpdate);
                    log.info("Saved a batch of {} analyzed passages.", batchToUpdate.size());
                    batchToUpdate.clear();
                }
                log.info("Successfully analyzed passage with id: {}", passage.getPassageId());

            } catch (ObjectNotFoundException e) {
                log.error("Failed to analyze passage {}: {}. Marking as analyzed to avoid retries.", passage.getPassageId(), e.getMessage());
                passage.setAnalyzed(true);
                batchToUpdate.add(passage);
            } catch (Exception e) {
                log.error("An unexpected error occurred while analyzing passage {}:", passage.getPassageId(), e);
            }
        }

        if (!batchToUpdate.isEmpty()) {
            passageRepository.saveAll(batchToUpdate);
        }
    }

    /**
     * Вычисляет метрики для одного конкретного прохождения,
     * сравнивая фактический путь с эталонным маршрутом
     * Рассчитывает покрытие, находит пропущенные и лишние точки,
     * а также точки, посещенные не в том порядке
     * @param passage Объект {@link PassageDocument} для анализа
     * @param route Соответствующий {@link RouteDocument}, содержащий эталонный список точек
     * @return Объект {@link PassageAnalytics} с рассчитанными метриками
     */
    public PassageAnalytics integrityCheck(PassageDocument passage, RouteDocument route) {

        List<String> routePointIds = route.getPointsId();
        List<String> passagePointsIds = passage.getVisitedPoints().stream()
                .map(VisitedPoint::getPointId).toList();

        Map<String, Long> routeFreq = routePointIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        Map<String, Long> passageFreq = passagePointsIds.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<String> missedPoints = new ArrayList<>();
        routeFreq.forEach((point, requiredCount) -> {
            long visitedCount = passageFreq.getOrDefault(point, 0L);
            if (visitedCount < requiredCount) {
                for (int i = 0; i < (requiredCount - visitedCount); i++) missedPoints.add(point);
            }
        });

        List<String> extraPoints = new ArrayList<>();
        passageFreq.forEach((point, visitedCount) -> {
            long requiredCount = routeFreq.getOrDefault(point, 0L);
            if (requiredCount < visitedCount) {
                for (int i = 0; i < (visitedCount - requiredCount); i++) extraPoints.add(point);
            }
        });

        List<String> lcsSequence = calculateLCS(routePointIds, passagePointsIds);
        List<String> outOfOrderPoints = new ArrayList<>(passagePointsIds);
        for (String lcsPoint : lcsSequence) {
            outOfOrderPoints.remove(lcsPoint);
        }
        for (String extraPoint : extraPoints) {
            outOfOrderPoints.remove(extraPoint);
        }

        double coverage = routePointIds.isEmpty() ? 0.0 : (double) (routePointIds.size() - missedPoints.size()) / routePointIds.size();
        double order = routePointIds.isEmpty() ? 0.0 : (double) lcsSequence.size() / routePointIds.size();

        return new PassageAnalytics(coverage, order, missedPoints, extraPoints, outOfOrderPoints);
    }

    /**
     * Алгоритм поиска наибольшей общей подпоследовательности (LCS)
     * Используется для определения, какая часть
     * точек маршрута была пройдена в правильной последовательности
     * @param routePointIds Эталонный список id точек
     * @param passagePointIds Фактический список id посещенных точек
     * @return Список id точек, представляющий собой LCS
     */
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

    /**
     * Вспомогательный метод для расчета средней продолжительности нахождения на точках
     * @param totalPointDurations Карта с суммарной длительностью на каждой точке
     * @param pointVisitCount Карта с количеством посещений каждой точки
     * @return Карта со средними значениями
     */
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

    /**
     * Вспомогательный метод для расчета средних значений
     * Возвращает `null`, если делитель равен `null` или нулю
     * @param total Общее значение
     * @param count Количество
     * @return Среднее значение или `null`.
     */
    private Double avg(Number total, Long count) {
        if (total == null || count == null || count == 0) {
            return null;
        }
        return total.doubleValue() / count;
    }
}
