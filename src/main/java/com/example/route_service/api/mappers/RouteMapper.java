package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.RouteDto;
import com.example.route_service.store.documents.RouteDocument;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RouteMapper {

    public RouteDocument toDocument(RouteDto dto, String userId) {
        return new RouteDocument(null, userId, dto.getPointsId());
    }

    public RouteDto toDto(RouteDocument route) {
        return new RouteDto(route.getUserId(), route.getPointsId());
    }
}