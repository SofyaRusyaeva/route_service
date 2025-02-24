package com.example.route_service.store.repositories;

import com.example.route_service.store.documents.PointDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PointRepository extends MongoRepository<PointDocument, String> {
    PointDocument findByPointId(String pointId);
}
