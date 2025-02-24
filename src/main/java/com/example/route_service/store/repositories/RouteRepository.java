package com.example.route_service.store.repositories;

import com.example.route_service.store.documents.RouteDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RouteRepository extends MongoRepository<RouteDocument, String> {
    List<RouteDocument> findByUserId(String userId);
}