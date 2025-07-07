package com.example.route_service.store.documents;


import com.example.route_service.store.documents.models.Analysis;
import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.enums.PassageStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "passage")
public class PassageDocument {
    @Id
    String passageId;

    @Indexed
    @Field("user_id")
    @NotBlank
    String userId;

    @Indexed
    @Field("route_id")
    @NotBlank
    String routeId;

    @Field("route_status")
    @NotNull
    PassageStatus passageStatus;

    @Field("start_time")
    @NotNull
    Instant startTime;

    @Field("end_time")
    @NotNull
    Instant endTime;

    @Field("visited_points")
    List<VisitedPoint> visitedPoints;

    @Field("feedback")
    Feedback feedback;

    @Field("analysis")
    Analysis analysis;
}