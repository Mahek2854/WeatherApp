package com.example.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "weather.api")
public class WeatherConfigProperties {

    private String baseUrl;
    private String onecallUrl;
    private String key;
    private String units = "metric";

    // Getters and Setters
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getOnecallUrl() { return onecallUrl; }
    public void setOnecallUrl(String onecallUrl) { this.onecallUrl = onecallUrl; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getUnits() { return units; }
    public void setUnits(String units) { this.units = units; }
}