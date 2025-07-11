package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PassageFeedbackRequest;
import com.example.route_service.api.dto.PassageResponse;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.models.Feedback;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PassageMapper {
    PassageResponse toResponse(PassageDocument document);

    Feedback toFeedback(PassageFeedbackRequest request);

}
