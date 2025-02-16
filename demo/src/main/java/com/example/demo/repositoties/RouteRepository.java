package com.example.demo.repositoties;

import com.example.demo.routes.Route;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RouteRepository extends MongoRepository<Route, String> {
    List<Route> findByUserId(ObjectId userId);
}