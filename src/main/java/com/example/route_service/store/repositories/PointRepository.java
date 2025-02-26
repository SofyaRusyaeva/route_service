package com.example.route_service.store.repositories;

import com.example.route_service.store.documents.PointDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PointRepository extends MongoRepository<PointDocument, String> {
    PointDocument findByPointId(String pointId);

    long countByPointIdIn(List<String> pointsId);
}