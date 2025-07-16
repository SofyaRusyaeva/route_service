package com.example.route_service.api.services;

import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.dto.RouteRequest;
import com.example.route_service.api.dto.RouteResponse;
import com.example.route_service.api.exeptions.InvalidObjectIdException;
import com.example.route_service.api.mappers.PointMapper;
import com.example.route_service.api.mappers.RouteMapper;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.documents.RouteDocument;
import com.example.route_service.store.documents.models.RouteAnalytics;
import com.example.route_service.store.repositories.PointRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;

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

    @BeforeEach
    void setUp() {
        lenient().when(authService.getCurrentUserId()).thenReturn("user1");
    }

//    @Test
//    void addRoute_whenRequestIsValid_shouldSucceed() {
//        // --- Arrange (Подготовка) ---
//        String userId = "user1";
//        List<String> pointIds = List.of("point1", "point2");
//        RouteRequest request = new RouteRequest(pointIds, false, new HashMap<>());
//
//        // Подготавливаем данные, которые будут "возвращать" репозитории
//        PointDocument point1 = new PointDocument();
//        point1.setPointId("point1");
//        PointDocument point2 = new PointDocument();
//        point2.setPointId("point2");
//        List<PointDocument> points = List.of(point1, point2);
//
//        RouteDocument documentToSave = new RouteDocument(); // Объект, который "вернет" routeMapper.toDocument
//        documentToSave.setPointsId(request.getPointsId());
//
//        RouteDocument savedDocument = new RouteDocument(); // Объект, который "вернет" routeRepository.save
//        savedDocument.setRouteId("route1");
//        savedDocument.setUserId(userId);
//        savedDocument.setPointsId(request.getPointsId());
//
//        // Подготавливаем финальный объект, который мы ожидаем получить от сервиса
//        RouteResponse expectedResponse = new RouteResponse("route1", userId, new ArrayList<>(), false, new HashMap<>());
//
//        // --- Mocking (Настройка моков) ---
//        // Настраиваем все зависимости, которые будут вызваны внутри addRoute
//
//        when(authService.getCurrentUserId()).thenReturn(userId);
//
//        // Мокируем вызов внутри validateAndGetPoints
//        when(pointRepository.findAllById(request.getPointsId())).thenReturn(points);
//
//        // Мокируем маппинг в документ
//        when(routeMapper.toDocument(request)).thenReturn(documentToSave);
//
//        // Мокируем сохранение в репозиторий
//        when(routeRepository.save(any(RouteDocument.class))).thenReturn(savedDocument);
//
//        // >>>>>>>>>>>> КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ <<<<<<<<<<<<
//        // Говорим мокам, что возвращать при финальном маппинге в DTO.
//        // Если этого не сделать, они вернут null.
//        // Нам не важны детали pointResponses для этого теста, поэтому можем вернуть пустой PointResponse.
//        when(pointMapper.toResponse(any(PointDocument.class))).thenReturn(new PointResponse());
//
//        // А вот финальный вызов routeMapper.toResponse важен. Говорим ему вернуть наш ожидаемый DTO.
//        when(routeMapper.toResponse(eq(savedDocument), anyList())).thenReturn(expectedResponse);
//        // eq(savedDocument) - убедиться, что маппится именно сохраненный документ.
//        // anyList() - нам не важен точный контент списка для этого мока.
//
//        // --- Act (Действие) ---
//        RouteResponse actualResponse = routeService.addRoute(request);
//
//        // --- Assert (Проверка) ---
//        assertNotNull(actualResponse, "The response from addRoute should not be null");
//        assertEquals(expectedResponse.getRouteId(), actualResponse.getRouteId());
//        assertEquals(expectedResponse.getUserId(), actualResponse.getUserId());
//
//        // Проверяем, что ID пользователя был установлен правильно перед сохранением
//        ArgumentCaptor<RouteDocument> routeCaptor = ArgumentCaptor.forClass(RouteDocument.class);
//        verify(routeRepository).save(routeCaptor.capture()); // "Ловим" объект, который передали в save
//        assertEquals(userId, routeCaptor.getValue().getUserId());
//
//        // Проверяем, что все нужные методы были вызваны
//        verify(authService, times(1)).getCurrentUserId();
//        verify(pointRepository, times(1)).findAllById(pointIds);
//        verify(routeRepository, times(1)).save(any(RouteDocument.class));
//        verify(routeMapper, times(1)).toResponse(eq(savedDocument), anyList());
//        verify(pointMapper, times(2)).toResponse(any(PointDocument.class)); // 2 раза, т.к. в списке 2 точки
//    }

//    @Test
//    void getMyRoutes() {
//    }
//
//    @Test
//    void getUserRoutes() {
//    }


    @Test
    void addRoute() {
        String userId = "user1";
        String routeId = "route1";
        List<String> pointIds = List.of("p1", "p2");
        RouteRequest request = new RouteRequest(pointIds, false, new HashMap<>());

        PointDocument point1 = new PointDocument("p1", null, null, null, null);
        PointDocument point2 = new PointDocument("p2", null, null, null, null);
        List<PointDocument> foundPoints = List.of(point1, point2);

        RouteDocument documentToSave = new RouteDocument(null, null, pointIds, true, new HashMap<>(), new RouteAnalytics());
        RouteDocument savedDocument = new RouteDocument(routeId, userId, pointIds, true, new HashMap<>(), new RouteAnalytics());
        RouteResponse expectedResponse = new RouteResponse(routeId, userId, List.of(new PointResponse(), new PointResponse()), true, new HashMap<>());

        when(pointRepository.findAllById(any())).thenReturn(foundPoints);
        when(routeMapper.toDocument(request)).thenReturn(documentToSave);
        when(routeRepository.save(any(RouteDocument.class))).thenReturn(savedDocument);
        when(routeMapper.toResponse(any(), anyList())).thenReturn(expectedResponse);

        RouteResponse response = routeService.addRoute(request);

        assertNotNull(response);
        assertEquals(expectedResponse.getRouteId(), response.getRouteId());

        ArgumentCaptor<RouteDocument> routeCaptor = ArgumentCaptor.forClass(RouteDocument.class);
        verify(routeRepository).save(routeCaptor.capture());
        RouteDocument capturedRoute = routeCaptor.getValue();

        assertNotNull(capturedRoute);
        assertEquals(userId, capturedRoute.getUserId());
        assertEquals(pointIds, routeCaptor.getValue().getPointsId());
        assertEquals(request.getDescription(), capturedRoute.getDescription());

        verify(authService, times(1)).getCurrentUserId();
        verify(routeRepository, times(1)).save(any(RouteDocument.class));
        verify(pointRepository, times(1)).findAllById(any());
        verify(routeMapper, times(1)).toDocument(any(RouteRequest.class));
        verify(routeMapper, times(1)).toResponse(any(RouteDocument.class), anyList());
    }

    @Test
    void addRouteWhenPointNotFound() {
        List<String> pointIds = List.of("p1", "p2");
        RouteRequest request = new RouteRequest(pointIds, true, new HashMap<>());
        List<PointDocument> foundPoints = List.of(new PointDocument("p1", null, null, null, null));

        when(pointRepository.findAllById(any())).thenReturn(foundPoints);

        assertThrows(InvalidObjectIdException.class, () -> {
            routeService.addRoute(request);
        });
        verify(routeRepository, never()).save(any());
    }


    //        when(pointRepository.findAllById(eq(pointIds))).thenReturn(List.of(new PointDocument("p1", null, null, null, null)));
//        doReturn(List.of(new PointDocument("p1", null, null, null, null))) // 1. Что вернуть
//                .when(pointRepository) // 2. На каком мок-объекте
//                .findAllById(eq(pointIds));
//    @Test
//    void updateRoute() {
//    }
//
//    @Test
//    void deleteRoute() {
//    }
//
//    @Test
//    void changeVisibility() {
//    }
}

/*

 @Test
    void whenAddRoute_withNonExistentPoint_shouldThrowException() {
        // --- Arrange ---
        List<String> pointIds = List.of("p1", "p2-non-existent");
        RouteRequest request = new RouteRequest(pointIds, true, new HashMap<>());

        // Мокируем так, будто репозиторий нашел только одну точку из двух
        when(pointRepository.findAllById(pointIds)).thenReturn(List.of(new PointDocument("p1", null, null, null, null)));

        // --- Act & Assert ---
        assertThrows(InvalidObjectIdException.class, () -> {
            routeService.addRoute(request);
        });

        // Убедимся, что маршрут даже не пытались сохранить
        verify(routeRepository, never()).save(any());
    }
 */