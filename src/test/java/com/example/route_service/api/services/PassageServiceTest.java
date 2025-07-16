package com.example.route_service.api.services;

import com.example.route_service.api.mappers.PassageMapper;
import com.example.route_service.store.repositories.PassageRepository;
import com.example.route_service.store.repositories.RouteRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Test
    void startPassage() {

    }
}