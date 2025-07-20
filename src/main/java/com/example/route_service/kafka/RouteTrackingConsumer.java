package com.example.route_service.kafka;

import com.example.route_service.kafka.events.PointEvent;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.models.VisitedPoint;
import com.example.route_service.store.repositories.PassageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Kafka-консьюмер, отвечающий за обработку событий отслеживания прохождения маршрута
 * Слушает топик `route-tracking-events` для получения в реальном времени
 * информации о перемещениях пользователя по точкам маршрута
 * На основе полученных событий обновляет соответствующий документ {@link PassageDocument},
 * фиксируя время прибытия и убытия с точек
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RouteTrackingConsumer {

    Logger log = LoggerFactory.getLogger(RouteTrackingConsumer.class);

    PassageRepository passageRepository;
    ObjectMapper objectMapper;

    /**
     * Основной метод-слушатель, который потребляет сообщения из топика Kafka
     * Преобразует входящее сообщение в {@link PointEvent}, находит соответствующее
     * прохождение и делегирует обработку события методам {@link #pointArrived} или
     * {@link #pointDeparted}
     *
     * @param msg Сырое сообщение из Kafka
     */
    @KafkaListener(topics = "route-tracking-events")
    public void consume(String msg) {
        try {
            PointEvent event = objectMapper.readValue(msg, PointEvent.class);
            log.info("Received event: {}", event.getEventType());

            passageRepository.findById(event.getPassageId()).ifPresentOrElse(
                    passage -> {
                        if ("POINT_ARRIVED".equals(event.getEventType())) {
                            pointArrived(passage, event);

                        } else if ("POINT_DEPARTED".equals(event.getEventType())) {
                            pointDeparted(passage, event);
                        }
                        passageRepository.save(passage);
                    },
                    () -> log.warn("Passage with id {} not found for event", event.getPassageId())
            );
        } catch (Exception e) {
            log.error("Failed to process message: {}", msg, e);
        }
    }

    /**
     * Обрабатывает событие прибытия пользователя на точку (POINT_ARRIVED)
     * Создает новый экземпляр {@link VisitedPoint}, устанавливает в нем время прибытия
     * и идентификатор точки и добавляет его в список посещенных точек прохождения
     * @param passage Прохождение {@link PassageDocument}, которое нужно обновить
     * @param event Событие {@link PointEvent}, содержащее детали прибытия
     */
    private void pointArrived(PassageDocument passage, PointEvent event) {

        VisitedPoint point = new VisitedPoint();

        point.setEntryTime(event.getTimestamp());
        point.setPointId(event.getPointId());

        passage.getVisitedPoints().add(point);

        log.info("POINT_ARRIVED for passage {}", passage.getPassageId());
    }

    /**
     * Обрабатывает событие ухода пользователя с точки (POINT_DEPARTED)
     * Находит последнюю посещенную точку с тем же pointId, у которой еще не установлено
     * время ухода и устанавливает его
     * @param passage Прохождение {@link PassageDocument}, которое нужно обновить
     * @param event Событие {@link PointEvent}, содержащее детали убытия
     */
    private void pointDeparted(PassageDocument passage, PointEvent event) {

        passage.getVisitedPoints().stream()
                .filter(point -> event.getPointId().equals(point.getPointId()) && point.getExitTime() == null)
                .findFirst().ifPresent(visitedPoint -> {
                    visitedPoint.setExitTime(event.getTimestamp());
                    log.info("POINT_DEPARTED for passage {}", passage.getPassageId());
                });
    }
}
