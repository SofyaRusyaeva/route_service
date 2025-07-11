package com.example.route_service.api.services;

import com.example.route_service.api.dto.PointRequest;
import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.StateException;
import com.example.route_service.api.mappers.PointMapper;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.repositories.PointRepository;
import com.example.route_service.store.repositories.RouteRepository;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PointService {
    PointRepository pointRepository;
    RouteRepository routeRepository;
    PointMapper pointMapper;


    public PointResponse getPointById(String pointId) {
        PointDocument point = pointRepository.findById(pointId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Point with id %s not found", pointId)));
        return pointMapper.toResponse(point);
    }

    public PointResponse updatePoint(String pointId, @Valid PointRequest newPoint) {
        PointDocument existingPoint = pointRepository.findById(pointId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Point with id %s not found", pointId)));

        existingPoint.setType(newPoint.getType());
        existingPoint.setLocation(newPoint.getLocation());
        existingPoint.setAddress(newPoint.getAddress());
        existingPoint.setLocationData(newPoint.getLocationData());

        PointDocument updatedPoint = pointRepository.save(existingPoint);
        return pointMapper.toResponse(updatedPoint);
    }

    public PointResponse addPoint(@Valid PointRequest point) {
        PointDocument pointDocument = pointRepository.save(pointMapper.toDocument(point));
        return pointMapper.toResponse(pointDocument);
//        try {
//            return pointRepository.save(pointMapper.toDocument(point));
//        } catch (Exception e) {
//            throw new ObjectSaveException("Error adding point");
//        }
    }

    public void deletePoint(String pointId) {


        if (!pointRepository.existsById(pointId)) {
            throw new ObjectNotFoundException(String.format("Point with id %s not found", pointId));
        }

        if (routeRepository.existsByPointsIdContains(pointId)) {
            throw new StateException(String.format("Point %s is in a route", pointId));
        }
        pointRepository.deleteById(pointId);
    }

}