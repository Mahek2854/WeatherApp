package com.example.weather.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "air_quality_data")
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

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Integer getAqi() { return aqi; }
    public void setAqi(Integer aqi) { this.aqi = aqi; }

    public Double getCo() { return co; }
    public void setCo(Double co) { this.co = co; }

    public Double getNo() { return no; }
    public void setNo(Double no) { this.no = no; }

    public Double getNo2() { return no2; }
    public void setNo2(Double no2) { this.no2 = no2; }

    public Double getO3() { return o3; }
    public void setO3(Double o3) { this.o3 = o3; }

    public Double getSo2() { return so2; }
    public void setSo2(Double so2) { this.so2 = so2; }

    public Double getPm25() { return pm25; }
    public void setPm25(Double pm25) { this.pm25 = pm25; }

    public Double getPm10() { return pm10; }
    public void setPm10(Double pm10) { this.pm10 = pm10; }

    public Double getNh3() { return nh3; }
    public void setNh3(Double nh3) { this.nh3 = nh3; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}