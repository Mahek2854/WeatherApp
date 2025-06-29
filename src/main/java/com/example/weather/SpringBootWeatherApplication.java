package com.example.weather;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.weather"}) // Ensure this matches your package structure
public class SpringBootWeatherApplication {

	public static void main(String[] args) {
		System.out.println("🚀 Starting Weather Application...");
		SpringApplication.run(SpringBootWeatherApplication.class, args);
		System.out.println("✅ Weather Application Started Successfully!");
	}
}
