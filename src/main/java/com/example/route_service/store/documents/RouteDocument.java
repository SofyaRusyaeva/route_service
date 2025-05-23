package com.example.route_service.store.documents;

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

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document(collection = "route")
public class RouteDocument {

    @Id
    String routeId;

    @NotBlank(message = "user_id cannot be blank")
    @Field("userId")
    String userId;

    @NotEmpty(message = "Points cannot be empty")
    @Field("pointsId")
    List<String>pointsId;

    @Field("isPublic")
    boolean isPublic = false;
}
