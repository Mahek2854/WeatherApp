package com.example.weather.controller;

import com.example.weather.entity.WeatherData;
import com.example.weather.service.WeatherService;
import com.example.weather.service.WeatherApiService; // Updated import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private WeatherApiService weatherApiService; // Updated field name

    @GetMapping("/current")
    public WeatherData getCurrentWeather(@RequestParam String city, @RequestParam String country) {
        try {
            return weatherService.getCompleteWeatherData(city, country);
        } catch (Exception e) {
            System.err.println("Error in WeatherController: " + e.getMessage());
            throw e;
        }
    }

    @GetMapping("/coordinates")
    public WeatherData getWeatherByCoordinates(@RequestParam double lat, @RequestParam double lon) {
        try {
            return weatherService.getWeatherByCoordinates(lat, lon);
        } catch (Exception e) {
            System.err.println("Error getting weather by coordinates: " + e.getMessage());
            throw e;
        }
    }

    @GetMapping("/test")
    public String testApi() {
        return "Weather API is working!";
    }
}