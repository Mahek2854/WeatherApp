package com.example.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherApiService {

    @Value("${weather.url}")
    private String weatherUrl;

    @Value("${weather.urlLat}")
    private String weatherUrlLat;

    @Value("${weather.key}")
    private String weatherKey;

    @Value("${weather.units}")
    private String weatherUnits;

    private final RestTemplate restTemplate;

    public WeatherApiService() {
        this.restTemplate = new RestTemplate();
    }

    public String getWeatherByCity(String city, String country) {
        try {
            String url = weatherUrl + city + "," + country + weatherKey + weatherUnits;
            System.out.println("🌐 WeatherAPI - Fetching by city: " + url.replace(weatherKey.substring(7), "***"));
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("❌ Error fetching weather by city: " + e.getMessage());
            throw e;
        }
    }

    public String getWeatherByCoordinates(double latitude, double longitude) {
        try {
            String url = weatherUrlLat + latitude + "&lon=" + longitude + weatherKey + weatherUnits;
            System.out.println("🌐 WeatherAPI - Fetching by coordinates: " + url.replace(weatherKey.substring(7), "***"));
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("❌ Error fetching weather by coordinates: " + e.getMessage());
            throw e;
        }
    }

    public String getWeatherUrl() {
        return weatherUrl;
    }

    public String getWeatherUrlLat() {
        return weatherUrlLat;
    }

    public String getWeatherKey() {
        return weatherKey;
    }

    public String getWeatherUnits() {
        return weatherUnits;
    }
}