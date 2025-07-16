package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PassageFeedbackRequest;
import com.example.route_service.api.dto.PassageResponse;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.enums.PassageStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PassageMapperTest {
    private PassageMapper passageMapper;

    @BeforeEach
    void setUp() {
        passageMapper = new PassageMapperImpl();
    }

    @Test
    void toResponse() {
        Instant now = Instant.now();
        Feedback feedback = new Feedback(4, "Good");
        PassageDocument document = new PassageDocument("passage1", "user1", "route1",
                PassageStatus.COMPLETED, now, now.plusSeconds(3600), new ArrayList<>(), feedback, null, true);

        PassageResponse response = passageMapper.toResponse(document);

        assertNotNull(response);
        assertNotNull(response.getFeedback());
        assertEquals("passage1", response.getPassageId());
        assertEquals("user1", response.getUserId());
        assertEquals("route1", response.getRouteId());
        assertEquals(PassageStatus.COMPLETED, response.getStatus());
        assertEquals(now, response.getStartTime());
        assertEquals(now.plusSeconds(3600), response.getEndTime());
        assertEquals(4, response.getFeedback().getRating());
        assertEquals("Good", response.getFeedback().getComment());
    }

    @Test
    void toFeedback() {
        PassageFeedbackRequest request = new PassageFeedbackRequest(4, "Good");

        Feedback feedback = passageMapper.toFeedback(request);

        assertNotNull(feedback);
        assertEquals(4, feedback.getRating());
        assertEquals("Good", feedback.getComment());
    }
}