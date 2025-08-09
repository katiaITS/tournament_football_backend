package com.tournament_football_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class TournamentFootballBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TournamentFootballBackendApplication.class, args);
	}
	@GetMapping("/")
	public String home() {
		return "Welcome to the Tournament Football API!";
	}
}
