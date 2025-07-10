package com.example.route_service.store.documents;

import com.example.route_service.store.documents.models.RouteAnalytics;
import io.swagger.v3.oas.annotations.media.Schema;
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

// TODO написать mapper

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "route")
public class RouteDocument {

    @Id
    String routeId;

    @Schema(description = "id пользователя создавшего маршрут")
    @NotBlank(message = "user_id cannot be blank")
    @Field("user_id")
    String userId;

    @Schema(description = "Список точек, составляющих маршрут")
    @NotEmpty(message = "Points cannot be empty")
    @Field("points_id")
    List<String>pointsId;

    @Schema(description = "Флаг публичности маршрута")
    @Field("is_public")
    boolean isPublic = false;

    @Schema(description = "Дополнительные сведения о маршруте (название, описание, рейтинг и т.д.)")
    @Field("description")
    HashMap<String, String> description;

    @Field("route_analytics")
    RouteAnalytics routeAnalytics = new RouteAnalytics();
}
