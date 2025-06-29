package com.example.weather.service;

import com.example.weather.entity.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LocationAPI {

    @Value("${location.url}")
    private String locationUrl;

    @Value("${location.key}")
    private String locationKey;

    @Value("${location.format}")
    private String locationFormat;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LocationAPI() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public Location getLocationByIP(String ipAddress) {
        try {
            String url = locationUrl + locationKey + "&ip=" + ipAddress + locationFormat;

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            Location location = new Location();
            location.setCity(jsonNode.get("cityName").asText());
            location.setCityName(jsonNode.get("cityName").asText());
            location.setCountry(jsonNode.get("countryName").asText());
            location.setLatitude(jsonNode.get("latitude").asDouble());
            location.setLongitude(jsonNode.get("longitude").asDouble());

            if (jsonNode.has("regionName")) {
                location.setState(jsonNode.get("regionName").asText());
            }

            return location;

        } catch (Exception e) {
            System.err.println("Error getting location by IP: " + e.getMessage());
            return getDefaultLocation();
        }
    }

    public Location getCurrentLocation() {
        // Get current location based on IP
        try {
            // This would typically get the client's IP address
            return getLocationByIP(""); // Empty IP will return location based on request IP
        } catch (Exception e) {
            return getDefaultLocation();
        }
    }

    private Location getDefaultLocation() {
        Location defaultLocation = new Location();
        defaultLocation.setCity("New York");
        defaultLocation.setCityName("New York");
        defaultLocation.setCountry("US");
        defaultLocation.setState("New York");
        defaultLocation.setLatitude(40.7128);
        defaultLocation.setLongitude(-74.0060);
        return defaultLocation;
    }

    public boolean isValidLocation(Location location) {
        return location != null &&
                location.getLatitude() != null &&
                location.getLongitude() != null &&
                location.getCity() != null &&
                !location.getCity().trim().isEmpty();
    }
}