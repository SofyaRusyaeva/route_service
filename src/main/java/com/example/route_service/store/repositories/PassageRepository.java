package com.example.route_service.store.repositories;

import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.enums.PassageStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface PassageRepository extends MongoRepository<PassageDocument, String> {

    Stream<PassageDocument> findAllByStatusAndIsAnalyzedIsFalse(PassageStatus status);

    List<PassageDocument> findAllByRouteIdAndStatus(String routeId, PassageStatus status);
}
