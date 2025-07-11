package com.example.route_service.store.documents.models;

import com.example.route_service.store.enums.PointStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VisitedPoint {

    @Field("point_id")
    @NotBlank
    String pointId;

    @Field("entry_time")
    @NotNull
    Instant entryTime;

    @Field("exit_time")
    Instant exitTime;

    @Field("point_status")
    PointStatus pointStatus;

    @Field("feedback")
    Feedback feedback;
}
