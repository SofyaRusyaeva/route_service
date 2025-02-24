package com.example.route_service.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class RouteResponseDto {

    String routeId;

    String userId;

    List<String> pointsId;
}
