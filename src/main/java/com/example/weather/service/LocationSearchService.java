package com.example.weather.service;

import com.example.weather.entity.Location;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationSearchService {

    @Value("${geocoding.api.url}")
    private String geocodingApiUrl;

    @Value("${geocoding.api.key}")
    private String geocodingApiKey;

    @Value("${geocoding.api.limit:5}")
    private int searchLimit;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public LocationSearchService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public List<Location> searchLocations(String query) {
        List<Location> locations = new ArrayList<>();

        try {
            String url = String.format("%s?q=%s&limit=%d&appid=%s",
                    geocodingApiUrl, query, searchLimit, geocodingApiKey);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonArray = objectMapper.readTree(response);

            for (JsonNode node : jsonArray) {
                Location location = new Location();
                location.setCityName(node.get("name").asText());
                location.setCity(node.get("name").asText());
                location.setCountry(node.get("country").asText());
                location.setLatitude(node.get("lat").asDouble());
                location.setLongitude(node.get("lon").asDouble());

                // Add state if available
                if (node.has("state")) {
                    location.setState(node.get("state").asText());
                }

                locations.add(location);
            }

        } catch (Exception e) {
            System.err.println("Error searching locations: " + e.getMessage());
        }

        return locations;
    }

    public Location getLocationDetails(String locationQuery) {
        List<Location> locations = searchLocations(locationQuery);

        if (!locations.isEmpty()) {
            return locations.get(0); // Return the first match
        }

        throw new RuntimeException("Location not found: " + locationQuery);
    }

    public Location getLocationByCoordinates(double latitude, double longitude) {
        try {
            String url = String.format("%s?lat=%f&lon=%f&limit=1&appid=%s",
                    geocodingApiUrl.replace("/direct", "/reverse"),
                    latitude, longitude, geocodingApiKey);

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonArray = objectMapper.readTree(response);

            if (jsonArray.isArray() && jsonArray.size() > 0) {
                JsonNode node = jsonArray.get(0);
                Location location = new Location();
                location.setCityName(node.get("name").asText());
                location.setCity(node.get("name").asText());
                location.setCountry(node.get("country").asText());
                location.setLatitude(latitude);
                location.setLongitude(longitude);

                if (node.has("state")) {
                    location.setState(node.get("state").asText());
                }

                return location;
            }

        } catch (Exception e) {
            System.err.println("Error getting location by coordinates: " + e.getMessage());
        }

        throw new RuntimeException("Location not found for coordinates: " + latitude + ", " + longitude);
    }
}