package com.example.route_service.api.services;

import com.example.route_service.api.dto.PassageRequest;
import com.example.route_service.api.dto.StartPassageResponse;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.StateException;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.models.Analysis;
import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

// TODO подумать над завершениями: два одинаковых метода

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PassageService {

    PassageRepository passageRepository;
    RouteRepository routeRepository;
    AuthService authService;
    AnalyticsService analyticsService;

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

        Analysis analysis = analyticsService.integrityCheck(passage);
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

        // надо в незавершенном маршруте сравнивать точки?
        Analysis analysis = analyticsService.integrityCheck(passage);
        passage.setAnalysis(analysis);

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

}
