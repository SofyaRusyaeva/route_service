### Вместо RouteDocument - RouteTemplateDocument - шаблон предполагаемого маршрута

*Нужны поля с названием, описанием, рейтингом маршрута?*

*Кто может создавать маршруты?*

*Добавить помимо id необходимую информацию о точках в документе маршрута?*

```java

@Document(collection = "route_template")
public class RouteTemplateDocument {

    @Id
    String routeId;

    @Field("creator_id")
    String creatorId;

    @NotEmpty
    @Field("points_id")
    List<String> pointsId;

    @Field("estimated_duration")
    int estimatedDuration;

    @Field("is_public")
    boolean isPublic = false;
}
```

### Точка без изменений

*Вместо Double latitude и longitude GeoJSON?*

*Смогут ли в дальнейшем пользователи добавлять точки? И в таком случае стоит ли добавить поле `source`?*

```java

@Document(collection = "point")
public class PointDocument {

    @Id
    String pointId;

    @Field
    @NotBlank(message = "Point type cannot be null")
    String type;

    @Field
    @Min(value = -90)
    @Max(value = 90)
    @NotNull(message = "Latitude cannot be null")
    Double latitude;

    @Field
    @Min(value = -180)
    @Max(value = 180)
    @NotNull(message = "Longitude cannot be null")
    Double longitude;

    @Field
    String address;

    @Valid
    @Field("location_data")
    LocationData locationData;
}
```

### ActualRoute - фактически пройденный маршрут

```java
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "actual_route")
public class ActualRouteDocument {

    @Id
    String routeId;

    @Indexed
    @Field("user_id")
    String userId;

    @Indexed
    @Field("template_id")
    String templateId;

    @Field("route_status")
    RouteStatus routeStatus;

    @Field("start_time")
    Instant startTime;

    @Field("end_time")
    Instant endTime;

    @Field("visited_points")
    List<VisietdPoint> visitedPoints;

    @Field("feedback")
    Feedback feedback;
}
```

### Фактически посещенная точка - вложенный документ

```java
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class VisitedPoint {

    //Ссылка на PointDocument
    @Field("point_id")
    String pointId;

    @Field("name")
    String name;

    @Field("entry_time")
    Instant entryTime;

    @Field("exit_time")
    Instant exitTime;

    @Field("point_status")
    PointStatus pointStatus;

    @Field("actual_location")
    GeoJsonPoint actualLocation;
}
```

### Обратная связь - вложенный документ

*Нужны теги?*

```java
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.mongodb.core.mapping.Field;

public class Feedback {
    @Min(1)
    @Max(5)
    @Field("rating")
    int rating;

    @Field("comment")
    String comment;
}
```

### Статус маршрута

```java
public enum RouteStatus {IN_PROGRESS, COMPLETED, CANCELLED}
```

### Статус точки

```java
public enum PointStatus {VISITED, SKIPPED}
```