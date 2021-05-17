package com.example.weather.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CoordAPI {

    @Value("${coord.url}")
    private String coordUrl;

    @Value("${coord.key}")
    private String coordKey;

    public String getCoordUrl() {
        return coordUrl;
    }

    public void setCoordUrl(String coordUrl) {
        this.coordUrl = coordUrl;
    }

    public String getCoordKey() {
        return coordKey;
    }

    public void setCoordKey(String coordKey) {
        this.coordKey = coordKey;
    }
}
