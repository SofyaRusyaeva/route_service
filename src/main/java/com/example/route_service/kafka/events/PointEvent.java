package com.example.route_service.kafka.events;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PointEvent {
    String eventType; // POINT_ARRIVED, POINT_DEPARTED
    String passageId;
    String pointId;
    Instant timestamp;
}
