package com.example.route_service;

import io.mongock.runner.springboot.EnableMongock;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableMongock
@EnableScheduling
public class RouteServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RouteServiceApplication.class, args);
	}

}