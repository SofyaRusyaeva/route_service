package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.RouteRequestDto;
import com.example.route_service.api.dto.RouteResponseDto;
import com.example.route_service.store.documents.RouteDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteMapper {

//    @Mapping(target = "routeId", ignore = true)
    RouteResponseDto toDto(RouteDocument entity);

    RouteDocument toEntity(RouteRequestDto dto);
}