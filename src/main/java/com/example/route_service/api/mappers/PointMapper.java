package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PointRequestDto;
import com.example.route_service.api.dto.PointResponseDto;
import com.example.route_service.store.documents.PointDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PointMapper {

//    @Mapping(target = "pointId", ignore = true)
    PointResponseDto toDto(PointDocument entity);

    PointDocument toEntity(PointRequestDto dto);
}