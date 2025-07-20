package com.example.route_service.store.documents;

import com.example.route_service.store.documents.models.RouteAnalytics;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.List;

/**
 * Документ маршрута, хранимый в коллекции "route"
 * Класс определяет структуру маршрута, включая его точки, владельца,
 * описание и агрегированную аналитику по всем его прохождениям
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "route")
public class RouteDocument {

    /**
     * Уникальный идентификатор маршрута
     */
    @Id
    String routeId;

    /**
     * Идентификатор пользователя, создавшего маршрут
     */
    @NotBlank(message = "user_id cannot be blank")
    @Field("user_id")
    String userId;

    /**
     * Список идентификаторов точек (PointDocument), составляющих маршрут
     */
    @NotEmpty(message = "Points cannot be empty")
    @Field("points_id")
    List<String>pointsId;

    /**
     * Флаг публичности маршрута, указывающий, является ли маршрут
     * публичным и доступным для всех пользователей
     * По умолчанию {@code false}
     */
    @Field("is_public")
    boolean isPublic = false;

    /**
     * Дополнительные сведения о маршруте (название, описание, рейтинг и т.д.)
     */
    @Field("description")
    HashMap<String, String> description;

    /**
     * Вложенный объект, содержащий агрегированную аналитику по всем прохождениям маршрута
     *
     * @see RouteAnalytics
     */
    @Field("route_analytics")
    RouteAnalytics routeAnalytics = new RouteAnalytics();
}
