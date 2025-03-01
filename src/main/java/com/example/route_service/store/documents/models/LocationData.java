package com.example.route_service.store.documents.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LocationData {

    @NotBlank(message = "Point name cannot be null")
    String name;

    String review;

    HashMap<String, String> attributions;
}