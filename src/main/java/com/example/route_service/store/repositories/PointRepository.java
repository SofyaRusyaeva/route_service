package com.example.route_service.store.repositories;

import com.example.route_service.store.documents.PointDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointRepository extends MongoRepository<PointDocument, String> {
    PointDocument findByPointId(String pointId);

    long countByPointIdIn(List<String> pointsId);
}