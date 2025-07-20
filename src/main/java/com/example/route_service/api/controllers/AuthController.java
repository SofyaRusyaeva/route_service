package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.AuthDto;
import com.example.route_service.api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST-контроллер, отвечающий за аутентификацию пользователей
 */
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Выполняет аутентификацию пользователя по логину и паролю
     *
     * @param authDto Объект {@link AuthDto}, содержащий логин и пароль пользователя
     * @return Строка, содержащая JSON-ответ от сервера авторизации в случае успешной
     * аутентификации (HTTP 200 OK)
     */
    @PostMapping("/auth")
    public String auth(@RequestBody AuthDto authDto) {
        return authService.authenticate(authDto.getLogin(), authDto.getPassword());
    }
}