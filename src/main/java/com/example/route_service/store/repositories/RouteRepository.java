package com.example.route_service.store.repositories;

import com.example.route_service.store.documents.RouteDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepository extends MongoRepository<RouteDocument, String> {
    List<RouteDocument> findByUserId(String userId);

    Optional<RouteDocument> findByUserIdAndRouteId(String userId, String routeId);

    List<RouteDocument> findAllByUserIdAndIsPublicTrue(String userId);
}