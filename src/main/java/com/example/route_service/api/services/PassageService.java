package com.example.route_service.api.services;

import com.example.route_service.api.dto.PassageFeedbackRequest;
import com.example.route_service.api.dto.PassageResponse;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.StateException;
import com.example.route_service.api.mappers.PassageMapper;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Сервис для управления жизненным циклом прохождения маршрутов
 * Отвечает за начало, завершение и отмену прохождений
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PassageService {

    PassageRepository passageRepository;
    RouteRepository routeRepository;
    AuthService authService;
    PassageMapper passageMapper;
    AtomicUpdateService atomicUpdateService;

    /**
     * Инициирует новое прохождение маршрута для текущего пользователя
     * Создает документ прохождения со статусом IN_PROGRESS и атомарно
     * увеличивает счетчик общего количества стартов для данного маршрута
     *
     * @param routeId Уникальный идентификатор маршрута, который пользователь начинает проходить
     * @return Объект {@link PassageResponse}, представляющий новое прохождение
     * @throws ObjectNotFoundException если маршрут с указанным id не существует
     */
    @Transactional
    public PassageResponse startPassage(String routeId) {
        String userId = authService.getCurrentUserId();

        if (!routeRepository.existsById(routeId)) {
            throw new ObjectNotFoundException(String.format("Route %s not found", routeId));
        }
        PassageDocument passage = new PassageDocument();

        passage.setUserId(userId);
        passage.setRouteId(routeId);
        passage.setStatus(PassageStatus.IN_PROGRESS);
        passage.setStartTime(Instant.now());

        atomicUpdateService.incStarts(routeId);
        return passageMapper.toResponse(passageRepository.save(passage));
    }

    /**
     * Завершает прохождение маршрута, устанавливая статус COMPLETED (только для прохождений со статусом IN_PROGRESS)
     * @param passageId Уникальный идентификатор завершаемого прохождения
     * @param request   Объект {@link PassageFeedbackRequest} с отзывом пользователя (может быть пустым)
     * @return Объект {@link PassageResponse} с обновленными данными прохождения
     * @throws ObjectNotFoundException если прохождение не найдено
     * @throws StateException если прохождение уже было завершено или отменено.
     * @throws AccessDeniedException если текущий пользователь не является владельцем прохождения
     */
    @Transactional
    public PassageResponse finishPassage(String passageId, PassageFeedbackRequest request) {
        String userId = authService.getCurrentUserId();
        PassageDocument passage = findAndValidateRoute(userId, passageId);

        passage.setEndTime(Instant.now());
        passage.setFeedback(passageMapper.toFeedback(request));
        passage.setStatus(PassageStatus.COMPLETED);
        return passageMapper.toResponse(passageRepository.save(passage));
    }

    /**
     * Отменяет прохождение маршрута, устанавливая статус CANCELLED (только для прохождений со статусом IN_PROGRESS)
     *
     * @param passageId Уникальный идентификатор отменяемого прохождения
     * @return Объект {@link PassageResponse} с обновленными данными прохождения
     * @throws ObjectNotFoundException если прохождение не найдено
     * @throws StateException          если прохождение уже было завершено или отменено
     * @throws AccessDeniedException   если текущий пользователь не является владельцем прохождения
     */
    @Transactional
    public PassageResponse cancelPassage(String passageId) {
        String userId = authService.getCurrentUserId();
        PassageDocument passage = findAndValidateRoute(userId, passageId);

        passage.setEndTime(Instant.now());
        passage.setStatus(PassageStatus.CANCELLED);

        PassageResponse response = passageMapper.toResponse(passageRepository.save(passage));
        atomicUpdateService.incCancellations(response.getRouteId());
        return response;
    }

    /**
     * Приватный метод для поиска прохождения и выполнения проверок
     * @param userId Идентификатор текущего пользователя для проверки прав доступа
     * @param passageId Идентификатор искомого прохождения
     * @return Документ {@link PassageDocument}, если все проверки пройдены
     * @throws ObjectNotFoundException если прохождение не найдено
     * @throws StateException если статус прохождения не IN_PROGRESS
     * @throws AccessDeniedException если пользователь пытается изменить чужое прохождение
     */
    private PassageDocument findAndValidateRoute(String userId, String passageId) {
        PassageDocument passage = passageRepository.findById(passageId).orElseThrow(
                () -> new ObjectNotFoundException(String.format("Passage %s not found", passageId))
        );

        if (passage.getStatus() != PassageStatus.IN_PROGRESS)
            throw new StateException("Passage is already finished or canceled");

        if (!passage.getUserId().equals(userId))
            throw new AccessDeniedException("User can't modify this passage");

        return passage;
    }
}
