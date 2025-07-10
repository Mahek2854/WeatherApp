package com.example.weather.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "radar_data")
@Getter
@Setter
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