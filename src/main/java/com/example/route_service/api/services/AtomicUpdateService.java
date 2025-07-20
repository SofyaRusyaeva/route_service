package com.example.route_service.api.services;

import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.PassageAnalytics;
import com.example.route_service.store.documents.models.VisitedPoint;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Сервис, предназначенный для выполнения атомарных операций обновления документов в MongoDB
 * Основная задача сервиса — обновление счетчиков и суммарных показателей в документе {@link RouteDocument} на основе
 * событий, происходящих в {@link PassageDocument}.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AtomicUpdateService {

    MongoTemplate mongoTemplate;

    /**
     * Атомарно увеличивает счетчик общего количества стартов для указанного маршрута
     *
     * @param routeId Уникальный идентификатор маршрута, для которого увеличивается счетчик
     */
    public void incStarts(String routeId) {
        Update update = new Update().inc("route_analytics.totalStarts", 1);
        executeUpdate(routeId, update);
    }

    /**
     * Атомарно увеличивает счетчик общего количества отмен прохождений для указанного маршрута
     * @param routeId Уникальный идентификатор маршрута, для которого увеличивается счетчик
     */
    public void incCancellations(String routeId) {
        Update update = new Update().inc("route_analytics.totalCancellations", 1);
        executeUpdate(routeId, update);
    }

    /**
     * Агрегирует данные из одного завершенного прохождения в общую аналитику маршрута
     * Атомарно обновляет поля в документе {@link RouteDocument} (счетчики завершений,
     * общие суммы рейтинга, продолжительности, покрытия, частотные карты для пропущенных,
     * лишних и посещенных не по порядку точек)
     * @param passage Объект {@link PassageDocument}, который был завершен и проанализирован
     */
    public void aggregatePassageAnalytics(PassageDocument passage) {
        if (passage.getRouteId() == null || passage.getPassageAnalytics() == null) {
            return;
        }
        log.info("Updating analytics for route {}. Passage analytics: {}", passage.getRouteId(), passage.getPassageAnalytics());
        Update update = new Update();
        update.inc("route_analytics.totalCompletions", 1);
//        update.inc("route_analytics.passagesAnalyzedCount", 1);

        if (passage.getFeedback() != null && passage.getFeedback().getRating() != null) {
            update.inc("route_analytics.totalRating", passage.getFeedback().getRating());
            update.inc("route_analytics.ratingsCount", 1);
        }

        if (passage.getStartTime() != null && passage.getEndTime() != null) {
            update.inc("route_analytics.totalDuration",
                    Duration.between(passage.getStartTime(), passage.getEndTime()).toSeconds());
        }

        PassageAnalytics passageAnalytics = passage.getPassageAnalytics();
        update.inc("route_analytics.totalCoverage", passageAnalytics.getCoverage());
        update.inc("route_analytics.totalOrder", passageAnalytics.getOrder());

        passageAnalytics.getMissedPoints().forEach(pointId ->
                update.inc("route_analytics.missedPointsFrequency." + pointId, 1L));
        passageAnalytics.getExtraPoints().forEach(pointId ->
                update.inc("route_analytics.extraPointsFrequency." + pointId, 1L));
        passageAnalytics.getOutOfOrderPoints().forEach(pointId ->
                update.inc("route_analytics.outOfOrderPointsFrequency." + pointId, 1L));

        for (VisitedPoint point : passage.getVisitedPoints()) {
            if (point.getPointId() != null && point.getEntryTime() != null && point.getExitTime() != null) {
                long durationOnPoint = Duration.between(point.getEntryTime(), point.getExitTime()).toSeconds();
                update.inc("route_analytics.totalPointDurations." + point.getPointId(), durationOnPoint);
                update.inc("route_analytics.pointVisitCount." + point.getPointId(), 1L);
            }
        }

        executeUpdate(passage.getRouteId(), update);
    }

    /**
     * Приватный метод, выполняющий операцию обновления в MongoDB
     * Создает запрос для поиска документа по `routeId` и применяет к нему
     * переданный объект {@link Update}
     * @param routeId Идентификатор документа {@link RouteDocument} для обновления
     * @param update  Объект {@link Update}, содержащий операции для выполнения (inc)
     */
    private void executeUpdate(String routeId, Update update) {
        Query query = new Query(Criteria.where("routeId").is(routeId));
        mongoTemplate.updateFirst(query, update, RouteDocument.class);
    }
}
