package com.example.route_service.api.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * DTO для запроса авторизации и аутентификации
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthDto {

    /**
     * Логин пользователя
     */
    String login;
    /**
     * Пароль пользователя
     */
    String password;
}