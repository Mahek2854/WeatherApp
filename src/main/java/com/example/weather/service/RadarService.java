package com.example.weather.service;

import com.example.weather.entity.RadarData;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RadarService {

    private final RestTemplate restTemplate;

    public RadarService() {
        this.restTemplate = new RestTemplate();
    }

    public RadarData getRadarData(double lat, double lon) {
        try {
            // Implement radar data fetching logic
            // This is a placeholder implementation
            RadarData radarData = new RadarData();
            radarData.setLatitude(lat);
            radarData.setLongitude(lon);
            return radarData;
        } catch (Exception e) {
            return new RadarData();
        }
    }
}