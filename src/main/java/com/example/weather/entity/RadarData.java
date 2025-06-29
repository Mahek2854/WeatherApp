package com.example.weather.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "radar_data")
public class RadarData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "precipitation")
    private Double precipitation;

    @Column(name = "intensity")
    private String intensity;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "radar_url")
    private String radarUrl;

    // Constructors
    public RadarData() {
        this.timestamp = LocalDateTime.now();
    }

    public RadarData(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // Missing methods: setLatitude and setLongitude
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getPrecipitation() { return precipitation; }
    public void setPrecipitation(Double precipitation) { this.precipitation = precipitation; }

    public String getIntensity() { return intensity; }
    public void setIntensity(String intensity) { this.intensity = intensity; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getRadarUrl() { return radarUrl; }
    public void setRadarUrl(String radarUrl) { this.radarUrl = radarUrl; }

    @Override
    public String toString() {
        return "RadarData{" +
                "id=" + id +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", precipitation=" + precipitation +
                ", intensity='" + intensity + '\'' +
                ", timestamp=" + timestamp +
                ", radarUrl='" + radarUrl + '\'' +
                '}';
    }
}