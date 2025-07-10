package com.example.weather.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "air_quality_data")
@Getter
@Setter
public class AirQualityData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "aqi")
    private Integer aqi; // Air Quality Index

    @Column(name = "co")
    private Double co; // Carbon Monoxide

    @Column(name = "no")
    private Double no; // Nitrogen Monoxide

    @Column(name = "no2")
    private Double no2; // Nitrogen Dioxide

    @Column(name = "o3")
    private Double o3; // Ozone

    @Column(name = "so2")
    private Double so2; // Sulfur Dioxide

    @Column(name = "pm2_5")
    private Double pm25; // PM2.5

    @Column(name = "pm10")
    private Double pm10; // PM10

    @Column(name = "nh3")
    private Double nh3; // Ammonia

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    // Constructors
    public AirQualityData() {
        this.timestamp = LocalDateTime.now();
    }

}