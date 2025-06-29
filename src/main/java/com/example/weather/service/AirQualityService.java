package com.example.weather.service;

import com.example.weather.entity.AirQualityData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AirQualityService {

    private final RestTemplate restTemplate;
    private static final String AIR_QUALITY_API_URL = "http://api.openweathermap.org/data/2.5/air_pollution";

    public AirQualityService() {
        this.restTemplate = new RestTemplate();
    }

    public AirQualityData getAirQuality(double lat, double lon) {
        try {
            String url = String.format("%s?lat=%f&lon=%f&appid=%s",
                    AIR_QUALITY_API_URL, lat, lon, getApiKey());

            // Make API call and parse response
            AirQualityData response = restTemplate.getForObject(url, AirQualityData.class);
            return response;
        } catch (Exception e) {
            // Return default/empty data on error
            return new AirQualityData();
        }
    }

    private String getApiKey() {
        // Return your API key - preferably from environment variables
        return System.getenv("OPENWEATHER_API_KEY");
    }
}