package com.example.route_service.migrations;

import com.example.route_service.store.documents.RouteDocument;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@ChangeUnit(id = "init-routes", order = "002", author = "Sofya")
public class RoutesMigration {

    @Execution
    public void execute(MongoTemplate mongoTemplate) {
        List<RouteDocument> routes = List.of(
                new RouteDocument(
                        null,
                        "9e9eca77-360e-4597-8429-87e7fc49a937",
                        List.of("686ae05a6c1af57a6e338384", "686ae05a6c1af57a6e338385", "686ae05a6c1af57a6e338386"),
                        true, null),
                new RouteDocument(
                        null,
                        "9e9eca77-360e-4597-8429-87e7fc49a937",
                        List.of("686ae05a6c1af57a6e338385", "686ae05a6c1af57a6e338386"),
                        false, null),
                new RouteDocument(
                        null,
                        "663e309c-1eb5-4171-8cf8-93e998b78c91",
                        List.of("686ae05a6c1af57a6e338384", "686ae05a6c1af57a6e338385"),
                        false, null
                )
        );
        mongoTemplate.insertAll(routes);
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("route_template");
    }
}
