package com.example.weather.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


public class WeatherAPI {

    @Value("${weather.urlLat}")
    private String urlLat;

    @Value("${weather.urlLon}")
    private String urlLon;

    @Value("${weather.urlExtra}")
    private String urlExtra;

    @Value("${weather.appKey}")
    private String appKey;

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getUrlLat() {
        return urlLat;
    }

    public void setUrlLat(String urlLat) {
        this.urlLat = urlLat;
    }

    public String getUrlLon() {
        return urlLon;
    }

    public void setUrlLon(String urlLon) {
        this.urlLon = urlLon;
    }

    public String getUrlExtra() {
        return urlExtra;
    }

    public void setUrlExtra(String urlExtra) {
        this.urlExtra = urlExtra;
    }
}
