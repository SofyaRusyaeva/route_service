package com.example.route_service.api.mappers;

import com.example.route_service.api.dto.PointRequest;
import com.example.route_service.api.dto.PointResponse;
import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.documents.models.LocationData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class PointMapperTest {

    private PointMapper pointMapper;

    @BeforeEach
    void setUp() {
        pointMapper = new PointMapperImpl();
    }

    @Test
    void toDocument() {
        LocationData locationData = new LocationData("парк Гагарина", "Хорошее место", new HashMap<>());
        GeoJsonPoint location = new GeoJsonPoint(50.199265, 53.227925);
        PointRequest request = new PointRequest("парк", location, "промышленный район", locationData);

        PointDocument document = pointMapper.toDocument(request);

        assertNotNull(document);
        assertNull(document.getPointId());
        assertNotNull(document.getLocationData());
        assertEquals("парк", document.getType());
        assertEquals(location, document.getLocation());
        assertEquals("промышленный район", document.getAddress());
        assertEquals("парк Гагарина", document.getLocationData().getName());
        assertEquals("Хорошее место", document.getLocationData().getReview());
    }

    @Test
    void toDocumentWhenGivenNullRequest() {
        PointRequest request = null;

        PointDocument document = pointMapper.toDocument(request);

        assertNull(document);
    }

    @Test
    void toResponse() {
        LocationData locationData = new LocationData("парк Гагарина", "Хорошее место", new HashMap<>());
        GeoJsonPoint location = new GeoJsonPoint(50.199265, 53.227925);
        PointDocument document = new PointDocument("point1", "парк", location, "промышленный район", locationData);

        PointResponse response = pointMapper.toResponse(document);

        assertNotNull(response);
        assertNotNull(response.getLocationData());
        assertEquals("point1", response.getPointId());
        assertEquals("парк", response.getType());
        assertEquals(location, response.getLocation());
        assertEquals("промышленный район", response.getAddress());
        assertEquals("Хорошее место", response.getLocationData().getReview());
        assertEquals("парк Гагарина", response.getLocationData().getName());
    }

    @Test
    void toResponseWhenGivenNullDocument() {
        PointDocument document = null;

        PointResponse response = pointMapper.toResponse(document);

        assertNull(response);
    }
}