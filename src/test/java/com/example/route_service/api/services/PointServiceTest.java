package com.example.route_service.api.services;

import com.example.route_service.api.dto.PointRequest;
import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.StateException;
import com.example.route_service.api.mappers.PointMapper;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.repositories.PointRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointServiceTest {

    @Mock
    PointRepository pointRepository;
    @Mock
    RouteRepository routeRepository;
    @Mock
    PointMapper pointMapper;

    @InjectMocks
    PointService pointService;

    @Test
    void getPointById_whenPointExists_shouldReturnPointResponse() {
        String pointId = "p1";
        PointDocument document = new PointDocument();
        PointResponse response = new PointResponse();

        when(pointRepository.findById(pointId)).thenReturn(Optional.of(document));
        when(pointMapper.toResponse(document)).thenReturn(response);

        PointResponse result = pointService.getPointById(pointId);

        assertNotNull(result);
        assertEquals(result, response);
    }

    @Test
    void getPointById_whenPointDoesNotExist_shouldThrowObjectNotFoundException() {
        String pointId = "p1-not-exists";
        when(pointRepository.findById(pointId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointService.getPointById(pointId))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("Point with id %s not found", pointId);
    }

    @Test
    void addPoint_shouldSaveAndReturnPoint() {
        PointRequest request = new PointRequest();
        PointDocument documentToSave = new PointDocument();
        PointDocument savedDocument = new PointDocument();
        PointResponse response = new PointResponse();

        when(pointMapper.toDocument(request)).thenReturn(documentToSave);
        when(pointRepository.save(documentToSave)).thenReturn(savedDocument);
        when(pointMapper.toResponse(savedDocument)).thenReturn(response);

        PointResponse result = pointService.addPoint(request);

        assertEquals(result, response);
        verify(pointRepository, times(1)).save(documentToSave);
    }

    @Test
    void deletePoint_whenPointExistsAndNotInRoute_shouldDeletePoint() {
        String pointId = "p1-to-delete";
        when(pointRepository.existsById(pointId)).thenReturn(true);
        when(routeRepository.existsByPointsIdContains(pointId)).thenReturn(false);

        pointService.deletePoint(pointId);

        verify(pointRepository, times(1)).deleteById(pointId);
    }

    @Test
    void deletePoint_whenPointDoesNotExist_shouldThrowObjectNotFoundException() {
        String pointId = "non-existent-id";
        when(pointRepository.existsById(pointId)).thenReturn(false);

        assertThatThrownBy(() -> pointService.deletePoint(pointId))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(pointRepository, never()).deleteById(any());
    }

    @Test
    void deletePoint_whenPointIsInRoute_shouldThrowStateException() {
        String pointId = "point-in-route";
        when(pointRepository.existsById(pointId)).thenReturn(true);
        when(routeRepository.existsByPointsIdContains(pointId)).thenReturn(true);

        assertThatThrownBy(() -> pointService.deletePoint(pointId))
                .isInstanceOf(StateException.class)
                .hasMessageContaining("Point %s is in a route", pointId);

        verify(pointRepository, never()).deleteById(any());
    }
}
