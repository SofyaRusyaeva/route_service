package com.example.demo.routes;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "route")
public class Route {
    @Id
    private String routeId;
    @Field("user_id")

    private String userId;
    @Field("points")
    private List<String> points;

    public Route() {
    }

    public Route(String routeId, String userId, List<String> points) {
        this.routeId = routeId;
        this.userId = userId;
        this.points = points;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getUserId() {
        return userId;
    }

    public List<String> getPoints() {
        return points;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setPoints(List<String> points) {
        this.points = points;
    }
}