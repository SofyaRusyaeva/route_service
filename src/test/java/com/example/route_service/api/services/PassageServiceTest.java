package com.example.route_service.api.services;

import com.example.route_service.api.dto.PassageFeedbackRequest;
import com.example.route_service.api.dto.PassageResponse;
import com.example.route_service.api.exeptions.ObjectNotFoundException;
import com.example.route_service.api.exeptions.StateException;
import com.example.route_service.api.mappers.PassageMapper;
import com.example.route_service.store.documents.PassageDocument;
import com.example.route_service.store.documents.models.Feedback;
import com.example.route_service.store.enums.PassageStatus;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class PassageServiceTest {

    @Mock
    PassageRepository passageRepository;
    @Mock
    RouteRepository routeRepository;
    @Mock
    AuthService authService;
    @Mock
    PassageMapper passageMapper;
    @Mock
    AtomicUpdateService atomicUpdateService;

    @InjectMocks
    private PassageService passageService;

    private static final String USER_ID = "user1";
    private static final String USER_ID_2 = "user2";
    private static final String ROUTE_ID = "route1";
    private static final String PASSAGE_ID = "passage1";

    @BeforeEach
    void setUp() {
        lenient().when(authService.getCurrentUserId()).thenReturn(USER_ID);
    }

    @Test
    void startPassage_whenRouteExists_shouldCreatePassageAndIncStarts() {
        // given
        when(routeRepository.existsById(ROUTE_ID)).thenReturn(true);
        // Используем ArgumentCaptor для перехвата объекта перед сохранением
        ArgumentCaptor<PassageDocument> passageCaptor = ArgumentCaptor.forClass(PassageDocument.class);

        when(passageRepository.save(any(PassageDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passageMapper.toResponse(any(PassageDocument.class))).thenReturn(new PassageResponse());

        passageService.startPassage(ROUTE_ID);

        verify(passageRepository).save(passageCaptor.capture());
        PassageDocument capturedPassage = passageCaptor.getValue();

        assertEquals(USER_ID, capturedPassage.getUserId());
        assertEquals(ROUTE_ID, capturedPassage.getRouteId());
        assertEquals(PassageStatus.IN_PROGRESS, capturedPassage.getStatus());
        assertNotNull(capturedPassage.getStartTime());
        assertFalse(capturedPassage.getStartTime().isAfter(Instant.now()));

        verify(atomicUpdateService, times(1)).incStarts(ROUTE_ID);
    }

    @Test
    void startPassage_whenRouteDoesNotExist_shouldThrowObjectNotFoundException() {
        when(routeRepository.existsById(ROUTE_ID)).thenReturn(false);

        assertThatThrownBy(() -> passageService.startPassage(ROUTE_ID))
                .isInstanceOf(ObjectNotFoundException.class);

        verify(passageRepository, never()).save(any());
        verify(atomicUpdateService, never()).incStarts(any());
    }

    @Test
    void finishPassage_whenPassageIsValid_shouldUpdateStatusAndFeedback() {
        PassageDocument passage = createPassageInProgress();
        PassageFeedbackRequest feedbackRequest = new PassageFeedbackRequest(5, "Great");
        Feedback feedback = new Feedback(5, "Great");

        when(passageRepository.findById(PASSAGE_ID)).thenReturn(Optional.of(passage));
        when(passageMapper.toFeedback(feedbackRequest)).thenReturn(feedback);
        when(passageRepository.save(any(PassageDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passageMapper.toResponse(any(PassageDocument.class))).thenReturn(new PassageResponse());

        passageService.finishPassage(PASSAGE_ID, feedbackRequest);

        ArgumentCaptor<PassageDocument> captor = ArgumentCaptor.forClass(PassageDocument.class);
        verify(passageRepository).save(captor.capture());
        PassageDocument savedPassage = captor.getValue();

        assertEquals(PassageStatus.COMPLETED, savedPassage.getStatus());
        assertEquals(feedback, savedPassage.getFeedback());
        assertNotNull(savedPassage.getEndTime());
    }

    @Test
    void cancelPassage_whenPassageIsValid_shouldUpdateStatusAndIncCancellations() {
        PassageDocument passage = createPassageInProgress();
        PassageResponse response = new PassageResponse();
        response.setRouteId(ROUTE_ID);

        when(passageRepository.findById(PASSAGE_ID)).thenReturn(Optional.of(passage));
        when(passageRepository.save(any(PassageDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(passageMapper.toResponse(any(PassageDocument.class))).thenReturn(response);

        passageService.cancelPassage(PASSAGE_ID);

        ArgumentCaptor<PassageDocument> captor = ArgumentCaptor.forClass(PassageDocument.class);
        verify(passageRepository).save(captor.capture());
        PassageDocument savedPassage = captor.getValue();

        assertEquals(PassageStatus.CANCELLED, savedPassage.getStatus());
        assertNotNull(savedPassage.getEndTime());

        verify(atomicUpdateService, times(1)).incCancellations(ROUTE_ID);
    }

    @Test
    void findAndValidate_whenPassageIsNotInProgress_shouldThrowStateException() {
        PassageDocument passage = createPassageInProgress();
        passage.setStatus(PassageStatus.COMPLETED);
        when(passageRepository.findById(PASSAGE_ID)).thenReturn(Optional.of(passage));

        assertThatThrownBy(() -> passageService.finishPassage(PASSAGE_ID, new PassageFeedbackRequest(null, null)))
                .isInstanceOf(StateException.class)
                .hasMessage("Passage is already finished or canceled");
    }

    @Test
    void findAndValidate_whenUserIsNotOwner_shouldThrowAccessDeniedException() {
        PassageDocument passage = createPassageInProgress();
        passage.setUserId(USER_ID_2);
        when(passageRepository.findById(PASSAGE_ID)).thenReturn(Optional.of(passage));

        assertThatThrownBy(() -> passageService.finishPassage(PASSAGE_ID, new PassageFeedbackRequest(null, null)))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User can't modify this passage");
    }

    private PassageDocument createPassageInProgress() {
        PassageDocument passage = new PassageDocument();
        passage.setPassageId(PASSAGE_ID);
        passage.setUserId(USER_ID);
        passage.setRouteId(ROUTE_ID);
        passage.setStatus(PassageStatus.IN_PROGRESS);
        return passage;
    }
}