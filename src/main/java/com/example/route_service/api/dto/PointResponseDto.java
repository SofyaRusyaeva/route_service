package com.example.route_service.api.dto;

import lombok.Data;

@Data
public class PointResponseDto {

    String pointId;

    String type;

    Double latitude;

    Double longitude;

    String address;
}
