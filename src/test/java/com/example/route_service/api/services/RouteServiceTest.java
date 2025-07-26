package com.example.route_service.api.services;

import com.example.route_service.api.dto.RouteRequest;
import com.example.route_service.api.dto.RouteResponse;
import com.example.route_service.api.exeptions.InvalidObjectIdException;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.mappers.PointMapper;
import com.example.route_service.api.mappers.RouteMapper;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.repositories.PointRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class RouteServiceTest {

    @Mock
    RouteRepository routeRepository;
    @Mock
    PointRepository pointRepository;
    @Mock
    AuthService authService;
    @Mock
    PointMapper pointMapper;
    @Mock
    RouteMapper routeMapper;

    @InjectMocks
    RouteService routeService;

    private static final String USER_ID = "user1";
    private static final String ROUTE_ID = "route1";
    private static final String POINT_ID_1 = "p1";
    private static final String POINT_ID_2 = "p2";

    @BeforeEach
    void setUp() {
        lenient().when(authService.getCurrentUserId()).thenReturn(USER_ID);
    }

    @Test
    void getMyRoutes_whenUserHasRoutes_shouldReturnRouteList() {
        RouteDocument routeDoc = new RouteDocument();
        routeDoc.setRouteId(ROUTE_ID);
        routeDoc.setPointsId(List.of(POINT_ID_1));

        PointDocument pointDoc = new PointDocument();
        pointDoc.setPointId(POINT_ID_1);

        when(routeRepository.findByUserId(USER_ID)).thenReturn(List.of(routeDoc));
        when(pointRepository.findAllById(Set.of(POINT_ID_1))).thenReturn(List.of(pointDoc));
        when(routeMapper.toResponse(any(), anyList())).thenReturn(new RouteResponse());

        List<RouteResponse> result = routeService.getMyRoutes();

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getMyRoutes_whenUserHasNoRoutes_shouldReturnEmptyList() {
        when(routeRepository.findByUserId(USER_ID)).thenReturn(Collections.emptyList());

        List<RouteResponse> result = routeService.getMyRoutes();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void addRoute_whenPointsAreValid_shouldCreateAndReturnRoute() {
        RouteRequest request = new RouteRequest(List.of(POINT_ID_1, POINT_ID_2), false, null);
        PointDocument point1 = new PointDocument(POINT_ID_1, "type", null, "addr", null);
        PointDocument point2 = new PointDocument(POINT_ID_2, "type", null, "addr", null);

        RouteDocument routeToSave = new RouteDocument();
        RouteDocument savedRoute = new RouteDocument();
        savedRoute.setRouteId(ROUTE_ID);

        when(pointRepository.findAllById(Set.of(POINT_ID_1, POINT_ID_2))).thenReturn(List.of(point1, point2));
        when(routeMapper.toDocument(request)).thenReturn(routeToSave);
        when(routeRepository.save(routeToSave)).thenReturn(savedRoute);
        when(routeMapper.toResponse(eq(savedRoute), anyList())).thenReturn(new RouteResponse());

        RouteResponse result = routeService.addRoute(request);

        assertNotNull(result);
        verify(routeRepository).save(routeToSave);
        assertEquals(USER_ID, routeToSave.getUserId());
    }

    @Test
    void addRoute_whenPointIdIsInvalid_shouldThrowInvalidObjectIdException() {
        RouteRequest request = new RouteRequest(List.of("invalid-point-id"), false, null);
        when(pointRepository.findAllById(Set.of("invalid-point-id"))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> routeService.addRoute(request))
                .isInstanceOf(InvalidObjectIdException.class);

        verify(routeRepository, never()).save(any());
    }

    @Test
    void deleteRoute_whenRouteExistsAndBelongsToUser_shouldDelete() {
        when(routeRepository.deleteByRouteIdAndUserId(ROUTE_ID, USER_ID)).thenReturn(1L);

        routeService.deleteRoute(ROUTE_ID);

        verify(routeRepository).deleteByRouteIdAndUserId(ROUTE_ID, USER_ID);
    }

    @Test
    void deleteRoute_whenRouteNotFoundOrNotBelongsToUser_shouldThrowObjectNotFoundException() {
        when(routeRepository.deleteByRouteIdAndUserId(ROUTE_ID, USER_ID)).thenReturn(0L);

        assertThatThrownBy(() -> routeService.deleteRoute(ROUTE_ID))
                .isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void changeVisibility_whenRouteExists_shouldSetPublicAndSave() {
        RouteDocument route = new RouteDocument();
        route.setRouteId(ROUTE_ID);
        route.setUserId(USER_ID);
        route.setPublic(false);

        when(routeRepository.findByUserIdAndRouteId(USER_ID, ROUTE_ID)).thenReturn(Optional.of(route));
        when(routeRepository.save(any(RouteDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(routeMapper.toResponse(any(), anyList())).thenReturn(new RouteResponse());

        routeService.changeVisibility(ROUTE_ID);

        verify(routeRepository).save(route);
        assertTrue(route.isPublic());
    }
}
