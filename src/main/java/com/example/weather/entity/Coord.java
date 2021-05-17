package com.example.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Coord {

    private Double longitude;
    private Double latitude;

    public Double getLongitude() {
        return longitude;
    }

    @JsonProperty("lon")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    @JsonProperty("lat")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("coord")
    public void setCoord(Map<String, Object> coord) {
        setLatitude((Double) coord.get("lat"));
        setLongitude((Double) coord.get("lon"));
    }
}
