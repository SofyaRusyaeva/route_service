package com.example.route_service.api.services;

import com.example.route_service.api.dto.PassageFeedbackRequest;
import com.example.route_service.api.dto.PassageResponse;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.StateException;
import com.example.route_service.api.mappers.PassageMapper;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PassageService {

    PassageRepository passageRepository;
    RouteRepository routeRepository;
    AuthService authService;
    PassageMapper passageMapper;
    MongoTemplate mongoTemplate;
    AtomicUpdateService atomicUpdateService;

    @Transactional
    public PassageResponse startPassage(String routeId) {

        String userId = authService.getCurrentUserId();

//        RouteDocument route = routeRepository.findById(routeId)
//                .orElseThrow(() -> new ObjectNotFoundException(String.format("Route %s not found", routeId)));
        if (!routeRepository.existsById(routeId)) {
            throw new ObjectNotFoundException(String.format("Route %s not found", routeId));
        }
        PassageDocument passage = new PassageDocument();

        passage.setUserId(userId);
        passage.setRouteId(routeId);
        passage.setStatus(PassageStatus.IN_PROGRESS);
        passage.setStartTime(Instant.now());

//        route.getRouteAnalytics().setTotalStarts(route.getRouteAnalytics().getTotalStarts() + 1);
//        routeRepository.save(route);
        atomicUpdateService.incStarts(routeId);
        return passageMapper.toResponse(passageRepository.save(passage));
    }

    @Transactional
    public PassageResponse finishPassage(String passageId, PassageFeedbackRequest request) {
        return updatePassageStatus(passageId, request, PassageStatus.COMPLETED);
    }

    @Transactional
    public PassageResponse cancelPassage(String passageId, PassageFeedbackRequest request) {
        PassageResponse response = updatePassageStatus(passageId, request, PassageStatus.CANCELLED);

        atomicUpdateService.incCancellations(response.getRouteId());
//        RouteDocument route = routeRepository.findById(response.getRouteId()).orElseThrow(() ->
//                new ObjectNotFoundException(String.format("Route %s not found", (response.getRouteId()))));
//        route.getRouteAnalytics().setTotalCancellations(route.getRouteAnalytics().getTotalCancellations() + 1);
//        routeRepository.save(route);

        return response;
    }

    private PassageResponse updatePassageStatus(String passageId, PassageFeedbackRequest request, PassageStatus status) {
        String userId = authService.getCurrentUserId();
        PassageDocument passage = findAndValidateRoute(userId, passageId);

        passage.setEndTime(Instant.now());
        passage.setFeedback(passageMapper.toFeedback(request));
        passage.setStatus(status);

        return passageMapper.toResponse(passageRepository.save(passage));
    }

    private PassageDocument findAndValidateRoute(String userId, String passageId) {
        PassageDocument passage = passageRepository.findById(passageId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Passage %s not found", passageId))
        );

        if (passage.getStatus() != PassageStatus.IN_PROGRESS)
            throw new StateException("Passage is already finished or canceled");

        if (!passage.getUserId().equals(userId))
            throw new AccessDeniedException("User can't modify this passage");

        return passage;
    }

    public void incrementRouteStarts(String routeId) {
        Query query = new Query(Criteria.where("routeId").is(routeId));
        Update update = new Update().inc("routeAnalytics.totalStarts", 1);
        mongoTemplate.updateFirst(query, update, RouteDocument.class);
    }
}
