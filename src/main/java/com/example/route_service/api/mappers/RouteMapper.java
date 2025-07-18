package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.api.dto.RouteRequest;
import com.example.route_service.api.dto.RouteResponse;
import com.example.route_service.store.documents.RouteDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RouteMapper {
    @Mapping(target = "routeId", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "routeAnalytics", ignore = true)
    RouteDocument toDocument(RouteRequest request);

    @Mapping(source = "routeDocument.routeId", target = "routeId")
    @Mapping(source = "routeDocument.userId", target = "userId")
//    @Mapping(source = "routeDocument.isPublic", target = "isPublic")
//    @Mapping(target = "isPublic", expression = "java(routeDocument.isPublic())")
    @Mapping(source = "routeDocument.description", target = "description")
    @Mapping(source = "points", target = "points")
    RouteResponse toResponse(RouteDocument routeDocument, List<PointResponse> points);
}
