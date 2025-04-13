package com.example.route_service.store.documents;

import com.example.route_service.store.documents.models.LocationData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
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