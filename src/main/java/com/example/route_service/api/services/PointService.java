package com.example.route_service.api.services;

import com.example.route_service.api.dto.PointDto;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.ObjectSaveException;
import com.example.route_service.api.mappers.PointMapper;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.repositories.PointRepository;
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
    private final PointMapper pointMapper;


    public PointDocument getPointById(String pointId) {
        return pointRepository.findById(pointId).orElseThrow(() -> new ObjectNotFoundException(String.format("Point with id %s not found", pointId)));
    }

    public PointDocument updatePoint(String pointId, @Valid PointDto newPoint) {
        return pointRepository.findById(pointId)
                .map(existingPoint -> {
                    existingPoint.setType(newPoint.getType());
                    existingPoint.setLatitude(newPoint.getLatitude());
                    existingPoint.setLongitude(newPoint.getLongitude());
                    existingPoint.setAddress(newPoint.getAddress());
                    existingPoint.setLocationData(newPoint.getLocationData());
                    return pointRepository.save(existingPoint);
                })
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Point with id %s not found", pointId)));
    }

    public PointDocument addPoint(@Valid PointDto point) {
        try {
            return pointRepository.save(pointMapper.toDocument(point));
        } catch (Exception e) {
            throw new ObjectSaveException("Error adding point");
        }
    }

    public void deletePoint(String pointId) {
        if (!pointRepository.existsById(pointId)) {
            throw new ObjectNotFoundException(String.format("Point with id %s not found", pointId));
        }
        pointRepository.deleteById(pointId);
    }

}