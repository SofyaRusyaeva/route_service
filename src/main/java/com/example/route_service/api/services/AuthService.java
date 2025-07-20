package com.example.route_service.api.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Сервис для авторизации и аутентификации пользователя
 */
@Service
public class AuthService {

    @Value("${client-id}")
    private String clientId;

    @Value("${client-secret}")
    private String clientSecret;

    @Value("${resource-url}")
    private String resourceServerUrl;

    @Value("${grant-type}")
    private String grantType;

    /**
     * Извлекает идентификатор текущего аутентифицированного пользователя из контекста безопасности
     *
     * @return Строку с уникальным идентификатором пользователя
     * @throws RuntimeException если пользователь не аутентифицирован
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        throw new RuntimeException("User is not authenticated");
    }

    /**
     * Выполняет аутентификацию пользователя, отправляя его учетные данные (логин и пароль)
     * на внешний сервер авторизации
     * @param login Логин пользователя
     * @param password Пароль пользователя
     * @return Строку, содержащую JSON с токенами доступа, если аутентификация прошла успешно и `null` в противном случае
     */
    public String authenticate(String login, String password) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var body = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&username=" + login +
                "&password=" + password +
                "&grant_type=" + grantType;

        var requestEntity = new HttpEntity<>(body, headers);
        var restTemplate = new RestTemplate();

        var response = restTemplate.exchange(
                resourceServerUrl,
                HttpMethod.POST,
                requestEntity,
                String.class
        );
        if (response.getStatusCode().value() == 200) {
            return response.getBody();
        }
        return null;
    }
}
