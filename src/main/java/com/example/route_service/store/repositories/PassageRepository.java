package com.example.route_service.store.repositories;

import com.example.route_service.store.documents.PassageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PassageRepository extends MongoRepository<PassageDocument, String> {
}
