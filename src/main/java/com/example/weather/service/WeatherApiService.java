package com.example.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import javax.annotation.PostConstruct;

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

    @PostConstruct
    public void validateConfiguration() {
        if (weatherKey == null || weatherKey.trim().isEmpty()) {
            System.err.println("❌ ERROR: Weather API key is not configured!");
            System.err.println("Please check weather.key property in application.properties");
        } else {
            System.out.println("✅ Weather API configured successfully");
            System.out.println("✅ Base URL: " + weatherUrl);
            System.out.println("✅ Coordinates URL: " + weatherUrlLat);
        }
    }

    public String getWeatherByCity(String city, String country) {
        try {
            String cityQuery = country != null && !country.trim().isEmpty()
                    ? city + "," + country
                    : city;

            String url = weatherUrl + cityQuery + weatherKey + weatherUnits;
            System.out.println("🌐 WeatherAPI - Fetching by city: " + url.replace(getApiKeyFromUrl(weatherKey), "***"));

            String response = restTemplate.getForObject(url, String.class);
            System.out.println("✅ Weather data fetched successfully for: " + cityQuery);
            return response;

        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP Error fetching weather by city: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 401) {
                System.err.println("❌ API Key Error - Please check your OpenWeatherMap API key");
            }
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error fetching weather by city: " + e.getMessage());
            throw e;
        }
    }

    public String getWeatherByCoordinates(double latitude, double longitude) {
        try {
            String url = weatherUrlLat + latitude + "&lon=" + longitude + weatherKey + weatherUnits;
            System.out.println("🌐 WeatherAPI - Fetching by coordinates: " + url.replace(getApiKeyFromUrl(weatherKey), "***"));

            String response = restTemplate.getForObject(url, String.class);
            System.out.println("✅ Weather data fetched successfully for coordinates: " + latitude + ", " + longitude);
            return response;

        } catch (HttpClientErrorException e) {
            System.err.println("❌ HTTP Error fetching weather by coordinates: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 401) {
                System.err.println("❌ API Key Error - Please check your OpenWeatherMap API key");
            }
            throw e;
        } catch (Exception e) {
            System.err.println("❌ Error fetching weather by coordinates: " + e.getMessage());
            throw e;
        }
    }

    private String getApiKeyFromUrl(String keyParam) {
        // Extract API key from &appid=key format
        if (keyParam != null && keyParam.contains("=")) {
            String[] parts = keyParam.split("=");
            if (parts.length > 1) {
                return parts[1];
            }
        }
        return keyParam;
    }

    public boolean isConfigured() {
        return weatherKey != null && !weatherKey.trim().isEmpty() &&
                weatherUrl != null && !weatherUrl.trim().isEmpty();
    }

    // Getters
    public String getWeatherUrl() { return weatherUrl; }
    public String getWeatherUrlLat() { return weatherUrlLat; }
    public String getWeatherKey() { return weatherKey; }
    public String getWeatherUnits() { return weatherUnits; }
}
