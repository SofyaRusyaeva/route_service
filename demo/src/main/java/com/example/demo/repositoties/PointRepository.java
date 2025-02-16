package com.example.demo.repositoties;

import com.example.demo.routes.Point;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PointRepository extends MongoRepository<Point, String> {
    Point findByPointId(String pointId);
}