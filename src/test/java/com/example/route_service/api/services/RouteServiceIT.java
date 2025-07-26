package com.example.route_service.api.services;

import com.example.route_service.api.dto.RouteRequest;
import com.example.route_service.api.dto.RouteResponse;
import com.example.route_service.api.exeptions.InvalidObjectIdException;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.repositories.PointRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class RouteServiceIT extends AbstractIntegrationTest {
    @Autowired
    RouteService routeService;
    @Autowired
    RouteRepository routeRepository;
    @Autowired
    PointRepository pointRepository;
    @Autowired
    AuthService authService;

    @AfterEach
    void tearDown() {
        routeRepository.deleteAll();
        pointRepository.deleteAll();
    }

    @Test
    void addRoute_whenPointsExist_shouldSaveRouteInDatabase() {
        PointDocument point1 = pointRepository.save(new PointDocument("p1", "type", null, "addr", null));
        PointDocument point2 = pointRepository.save(new PointDocument("p2", "type", null, "addr", null));

        List<String> pointIds = List.of(point1.getPointId(), point2.getPointId());
        RouteRequest request = new RouteRequest(pointIds, true, new HashMap<>());

        RouteResponse createdRouteResponse = routeService.addRoute(request);

        assertNotNull(createdRouteResponse);
        assertEquals(2, createdRouteResponse.getPoints().size());

        Optional<RouteDocument> foundRouteOpt = routeRepository.findById(createdRouteResponse.getRouteId());
        assertTrue(foundRouteOpt.isPresent());

        RouteDocument foundRouteInDb = foundRouteOpt.get();
        assertEquals("user1", foundRouteInDb.getUserId());
//        assertThat(foundRouteInDb.getPointsId()).containsExactly(point1.getPointId(), point2.getPointId());
        assertTrue(foundRouteInDb.isPublic());
    }

    @Test
    void addRoute_whenPointDoesNotExist_shouldThrowExceptionAndNotSave() {
        PointDocument point1 = pointRepository.save(new PointDocument("p1", "type", null, "addr", null));
        RouteRequest request = new RouteRequest(List.of(point1.getPointId(), "p2-not-exist"), true, null);

        assertThatThrownBy(() -> routeService.addRoute(request))
                .isInstanceOf(InvalidObjectIdException.class);

        assertEquals(0, routeRepository.count());
    }

    @Test
    void deleteRoute_whenUserIsOwner_shouldDeleteRouteFromDatabase() {
        RouteDocument route = new RouteDocument();
        route.setUserId("user1");
        route.setPointsId(Collections.emptyList());
        RouteDocument savedRoute = routeRepository.save(route);

        assertTrue(routeRepository.existsById(savedRoute.getRouteId()));

        routeService.deleteRoute(savedRoute.getRouteId());

        assertFalse(routeRepository.existsById(savedRoute.getRouteId()));
    }

    @Test
    void deleteRoute_whenUserIsNotOwner_shouldThrowExceptionAndNotDelete() {
        RouteDocument route = new RouteDocument();
        route.setUserId("user2");
        RouteDocument savedRoute = routeRepository.save(route);

        assertThatThrownBy(() -> routeService.deleteRoute(savedRoute.getRouteId()))
                .isInstanceOf(ObjectNotFoundException.class);

        assertTrue(routeRepository.existsById(savedRoute.getRouteId()));
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
