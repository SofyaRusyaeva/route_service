package com.example.demo.routes;

import com.example.demo.routes.models.LocationData;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "point")
public class Point {
    @Id
    private String pointId;
    @Field("location_data")
    private LocationData locationData;

    public Point() {}

    public Point(String pointId, LocationData locationData) {
        this.pointId = pointId;
        this.locationData = locationData;
    }

    public String getPointId() {
        return pointId;
    }

    public LocationData getLocationData() {
        return locationData;
    }

    public void setPointId(String pointId) {
        this.pointId = pointId;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }
}