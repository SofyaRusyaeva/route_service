package com.example.route_service.api.controllers;

import com.example.route_service.api.dto.AuthDto;
import com.example.route_service.api.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth")
    public String auth(@RequestBody AuthDto authDto) {
        return authService.authenticate(authDto.getLogin(), authDto.getPassword());
    }
}