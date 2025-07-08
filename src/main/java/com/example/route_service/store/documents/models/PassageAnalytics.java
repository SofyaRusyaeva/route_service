package com.example.route_service.store.documents.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PassageAnalytics {

    double coverage;

    double order;

    List<String> missedPoints;

    List<String> extraPoints;

    List<String> outOfOrderPoints;
}
