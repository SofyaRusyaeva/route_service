package com.example.route_service.api.services;

import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.PassageAnalytics;
import com.example.route_service.store.documents.models.VisitedPoint;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AtomicUpdateService {

    MongoTemplate mongoTemplate;

    public void incStarts(String routeId) {
        Update update = new Update().inc("routeAnalytics.totalStarts", 1);
        executeUpdate(routeId, update);
    }

    public void incCancellations(String routeId) {
        Update update = new Update().inc("routeAnalytics.totalCancellations", 1);
        executeUpdate(routeId, update);
    }

    public void aggregatePassageAnalytics(PassageDocument passage) {
        if (passage.getRouteId() == null || passage.getPassageAnalytics() == null) {
            return;
        }

        Update update = new Update();
        update.inc("routeAnalytics.totalCompletions", 1);
        update.inc("routeAnalytics.passagesAnalyzedCount", 1);

        if (passage.getFeedback() != null && passage.getFeedback().getRating() != null) {
            update.inc("routeAnalytics.totalRating", passage.getFeedback().getRating());
            update.inc("routeAnalytics.ratingsCount", 1);
        }

        if (passage.getStartTime() != null && passage.getEndTime() != null) {
            update.inc("routeAnalytics.totalDuration",
                    Duration.between(passage.getStartTime(), passage.getEndTime()).toSeconds());
        }

        PassageAnalytics passageAnalytics = passage.getPassageAnalytics();
        update.inc("routeAnalytics.totalCoverage", passageAnalytics.getCoverage());
        update.inc("routeAnalytics.totalOrder", passageAnalytics.getOrder());

        passageAnalytics.getMissedPoints().forEach(pointId ->
                update.inc("routeAnalytics.missedPointsFrequency." + pointId, 1L));
        passageAnalytics.getExtraPoints().forEach(pointId ->
                update.inc("routeAnalytics.extraPointsFrequency." + pointId, 1L));
        passageAnalytics.getOutOfOrderPoints().forEach(pointId ->
                update.inc("routeAnalytics.outOfOrderPointsFrequency." + pointId, 1L));


        for (VisitedPoint point : passage.getVisitedPoints()) {
            if (point.getPointId() != null && point.getEntryTime() != null && point.getExitTime() != null) {
                long durationOnPoint = Duration.between(point.getEntryTime(), point.getExitTime()).toSeconds();
                update.inc("routeAnalytics.totalPointDurations." + point.getPointId(), durationOnPoint);
                update.inc("routeAnalytics.pointVisitCount." + point.getPointId(), 1L);
            }
        }

        executeUpdate(passage.getRouteId(), update);
    }

    private void executeUpdate(String routeId, Update update) {
        Query query = new Query(Criteria.where("routeId").is(routeId));
        mongoTemplate.updateFirst(query, update, RouteDocument.class);
    }
}
