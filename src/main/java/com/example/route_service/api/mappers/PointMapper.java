package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PointDto;
import com.example.route_service.store.documents.PointDocument;
import org.springframework.stereotype.Component;

@Component
public class PointMapper {
    public PointDocument toDocument(PointDto dto) {
        return new PointDocument(
                null,
                dto.getType(),
                dto.getLocation(),
                dto.getAddress(),
                dto.getLocationData()
        );
    }
}
