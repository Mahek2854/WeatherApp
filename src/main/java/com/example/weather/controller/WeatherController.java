package com.example.weather.controller;

import com.example.weather.entity.WeatherData;
import com.example.weather.service.WeatherService;
import com.example.weather.service.WeatherApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherApiService weatherApiService;

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather(
            @RequestParam String city,
            @RequestParam(required = false) String country) {
        try {
            System.out.println("🌤️ Getting weather for: " + city + (country != null ? ", " + country : ""));

            WeatherData weather = weatherService.getCompleteWeatherData(city, country);

            if (weather != null) {
                System.out.println("✅ Weather data retrieved successfully");
                return ResponseEntity.ok(weather);
            } else {
                System.err.println("❌ No weather data found");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("❌ Error in WeatherController: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch weather data");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/coordinates")
    public ResponseEntity<?> getWeatherByCoordinates(
            @RequestParam double lat,
            @RequestParam double lon) {
        try {
            System.out.println("🌍 Getting weather for coordinates: " + lat + ", " + lon);

            WeatherData weather = weatherService.getWeatherByCoordinates(lat, lon);

            if (weather != null) {
                System.out.println("✅ Weather data retrieved successfully");
                return ResponseEntity.ok(weather);
            } else {
                System.err.println("❌ No weather data found");
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            System.err.println("❌ Error getting weather by coordinates: " + e.getMessage());
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to fetch weather data by coordinates");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testApi() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Weather API is working!");
        response.put("timestamp", System.currentTimeMillis());
        response.put("apiConfigured", weatherApiService.isConfigured());

        System.out.println("🧪 API test endpoint called");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/config/status")
    public ResponseEntity<Map<String, Object>> getConfigStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("weatherApiConfigured", weatherApiService.isConfigured());
        status.put("weatherUrl", weatherApiService.getWeatherUrl());
        status.put("weatherUrlLat", weatherApiService.getWeatherUrlLat());
        status.put("hasApiKey", weatherApiService.getWeatherKey() != null && !weatherApiService.getWeatherKey().trim().isEmpty());

        return ResponseEntity.ok(status);
    }
}
