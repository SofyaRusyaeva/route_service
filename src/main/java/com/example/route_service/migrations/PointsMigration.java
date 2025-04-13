package com.example.route_service.migrations;

import com.example.route_service.store.documents.PointDocument;
import com.example.route_service.store.documents.models.LocationData;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ChangeUnit(id = "init-points", order = "001", author = "Sofya")
public class PointsMigration {

    @Execution
    public void execute(MongoTemplate mongoTemplate) {
        List<PointDocument> points = List.of(
                createParkPoint(
                        "Парк",
                        53.228981,
                        50.169918,
                        "Загородный центральный парк культуры и отдыха имени М. Горького, Октябрьский район, городской округ Самара",
                        "Центральный парк культуры и отдыха им. М. Горького",
                        "Центральный парк культуры и отдыха им. М. Горького (Загородный парк) — самый большой парк в Самаре, государственный памятник природы. Парк площадью 42,4 гектара располагается между берегом реки Волги и Ново-Садовой улицей",
                        Map.of(
                                "opening_hours", "10:00-20:00",
                                "website", "https://samarazagorod.narod.ru/"
                        )),
                createParkPoint(
                        "Парк",
                        53.227925,
                        50.199265,
                        "парк культуры и отдыха имени Юрия Гагарина, Промышленный район, городской округ Самара",
                        "Парк культуры и отдыха имени Ю. А. Гагарина",
                        "Парк культуры и отдыха имени Ю. А. Гагарина — это популярное место для семейного отдыха в Самаре.",
                        Map.of(
                                "opening_hours", "Круглосуточно",
                                "website", "https://parki-samara.ru/park-im-yu-gagarina/"
                        )),
                createParkPoint(
                        "Парк",
                        53.243263,
                        50.222251,
                        "парк Воронежские озёра, Промышленный район, городской округ Самара",
                        "Воронежские озера",
                        "«Воронежские озёра» — это парк культуры и отдыха, расположенный в Промышленном районе Самары. Он включает в себя три озера и многолетние дубы, признанные памятниками природы.",
                        Map.of(
                                "opening_hours", "Круглосуточно",
                                "phone", "+7 (927) 751-77-78"
                        ))
        );
        mongoTemplate.insertAll(points);
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("point");
    }

    private PointDocument createParkPoint(
            String type,
            double latitude,
            double longitude,
            String address,
            String name,
            String review,
            Map<String, String> attributes
    ) {
        LocationData location = new LocationData(name, review, new HashMap<>(attributes));
        return new PointDocument(null, type, latitude, longitude, address, location);
    }
}