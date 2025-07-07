package com.example.route_service.store.documents;

import com.example.route_service.store.documents.models.LocationData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
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

    @Field("type")
    @NotBlank(message = "Point type cannot be null")
    String type;

    @Field("location")
    GeoJsonPoint location;

    @Field("address")
    String address;

    @Valid
    @Field("location_data")
    LocationData locationData;
}