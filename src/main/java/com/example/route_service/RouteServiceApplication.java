package com.example.route_service;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO Вернуть GlobalExceptionHandler
// TODO дописать документацию
// TODO вернуть авторизацию в SecurityConfig

@SpringBootApplication
@EnableMongock
public class RouteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteServiceApplication.class, args);
	}

}