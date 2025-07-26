package com.example.route_service.api.services;

import com.example.route_service.api.dto.PassageFeedbackRequest;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassageServiceIT extends AbstractIntegrationTest {
    @Autowired
    private PassageService passageService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private PassageRepository passageRepository;
    @Autowired
    private RouteRepository routeRepository;

    @AfterEach
    void tearDown() {
        passageRepository.deleteAll();
        routeRepository.deleteAll();
    }

    @Test
    void startPassage_whenRouteExists_shouldCreatePassageAndIncrementStartsCounter() {
        RouteDocument route = new RouteDocument();
        route.setUserId("user");
        route.setPublic(true);
        RouteDocument savedRoute = routeRepository.save(route);
        String routeId = savedRoute.getRouteId();

        passageService.startPassage(routeId);


        List<PassageDocument> passages = passageRepository.findAll();
        assertEquals(1, passages.size());
        PassageDocument createdPassage = passages.get(0);
        assertEquals(routeId, createdPassage.getRouteId());
        assertEquals("user1", createdPassage.getUserId());
        assertEquals(PassageStatus.IN_PROGRESS, createdPassage.getStatus());

        Optional<RouteDocument> updatedRouteOpt = routeRepository.findById(routeId);
        assertTrue(updatedRouteOpt.isPresent());
        assertEquals(1, updatedRouteOpt.get().getRouteAnalytics().getTotalStarts());
    }

    @Test
    void finishPassage_whenUserIsOwner_shouldUpdateStatusToCompleted() {
        PassageDocument passage = new PassageDocument();
        passage.setUserId("user1");
        passage.setStatus(PassageStatus.IN_PROGRESS);
        PassageDocument savedPassage = passageRepository.save(passage);
        String passageId = savedPassage.getPassageId();

        PassageFeedbackRequest feedbackRequest = new PassageFeedbackRequest(5, "Great");

        passageService.finishPassage(passageId, feedbackRequest);


        Optional<PassageDocument> updatedPassageOpt = passageRepository.findById(passageId);
        assertThat(updatedPassageOpt).isPresent();
        PassageDocument updatedPassage = updatedPassageOpt.get();

        assertEquals(PassageStatus.COMPLETED, updatedPassage.getStatus());
        assertNotNull(updatedPassage.getEndTime());
        assertNotNull(updatedPassage.getFeedback());
        assertEquals(5, updatedPassage.getFeedback().getRating());
        assertEquals("Great", updatedPassage.getFeedback().getComment());
    }

    @Test
    void finishPassage_whenUserIsNotOwner_shouldThrowAccessDenied() {
        PassageDocument passage = new PassageDocument();
        passage.setUserId("user2");
        passage.setStatus(PassageStatus.IN_PROGRESS);
        PassageDocument savedPassage = passageRepository.save(passage);
        String passageId = savedPassage.getPassageId();

        assertThatThrownBy(() -> passageService.finishPassage(passageId, new PassageFeedbackRequest(null, null)))
                .isInstanceOf(AccessDeniedException.class);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public AuthService testAuthService() {
            AuthService mockAuthService = mock(AuthService.class);
            when(mockAuthService.getCurrentUserId()).thenReturn("user1");
            return mockAuthService;
        }

        @Bean
        @Primary
        public JwtDecoder jwtDecoder() {
            return mock(JwtDecoder.class);
        }
    }
}
