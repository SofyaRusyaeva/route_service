package com.example.route_service.store.documents;


import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.documents.models.PassageAnalytics;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Документ конкретного прохождения пользователем маршрута, хранимый в коллекции "passage"
 * Содержит метрики и данные этого прохождения
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "passage")
public class PassageDocument {
    /**
     * Уникальный идентификатор прохождения
     */
    @Id
    String passageId;

    /**
     * Идентификатор пользователя, проходящего маршрут
     */
    @Indexed
    @Field("user_id")
    @NotBlank
    String userId;

    /**
     * Идентификатор проходимого маршрута
     */
    @Indexed
    @Field("route_id")
    @NotBlank
    String routeId;

    /**
     * Текущий статус прохождения (IN_PROGRESS, COMPLETED, CANCELLED)
     */
    @Field("route_status")
    @NotNull
    PassageStatus status;

    /**
     * Время начала прохождения маршрута
     */
    @Field("start_time")
    Instant startTime;

    /**
     * Время окончания прохождения маршрута (устанавливается при завершении или отмене)
     */
    @Field("end_time")
    Instant endTime;

    /**
     * Список посещенных точек с характеристиками по каждой
     *
     * @see VisitedPoint
     */
    @Field("visited_points")
    List<VisitedPoint> visitedPoints = new ArrayList<>();

    /**
     * Отзыв, оставленный пользователем по прохождении маршрута
     * @see Feedback
     */
    @Field("feedback")
    Feedback feedback;

    /**
     * Аналитика, рассчитанная по данному прохождению
     * @see PassageAnalytics
     */
    @Field("passage_analytics")
    PassageAnalytics passageAnalytics;

    /**
     * Флаг, указывающий была ли проведена аналитика данного прохождения
     * Пол умолчанию {@code false}
     */
    @Field("is_analyzed")
    boolean isAnalyzed = false;
}