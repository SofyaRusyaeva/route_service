package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PointRequest;
import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.store.documents.PointDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(
        componentModel = "spring"
//        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface PointMapper {
    @Mapping(target = "pointId", ignore = true)
    PointDocument toDocument(PointRequest request);

    PointResponse toResponse(PointDocument document);
}
