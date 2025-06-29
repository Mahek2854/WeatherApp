package com.example.weather.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recent_locations")
public class RecentLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "country")
    private String country;

    @Column(name = "state")
    private String state;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "search_time")
    private LocalDateTime searchTime;

    @Column(name = "last_accessed")
    private LocalDateTime lastAccessed;

    // Constructors
    public RecentLocation() {
        this.searchTime = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
    }

    public RecentLocation(String sessionId, Location location) {
        this.sessionId = sessionId;
        this.cityName = location.getCityName();
        this.country = location.getCountry();
        this.state = location.getState();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.searchTime = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
    }

    // Constructor with 5 parameters (the one being called in RecentLocationService)
    public RecentLocation(String sessionId, String cityName, String country, Double latitude, Double longitude) {
        this.sessionId = sessionId;
        this.cityName = cityName;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.searchTime = LocalDateTime.now();
        this.lastAccessed = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public LocalDateTime getSearchTime() { return searchTime; }
    public void setSearchTime(LocalDateTime searchTime) { this.searchTime = searchTime; }

    // Missing method: getLastAccessed and setLastAccessed
    public LocalDateTime getLastAccessed() { return lastAccessed; }
    public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }

    @Override
    public String toString() {
        return "RecentLocation{" +
                "id=" + id +
                ", sessionId='" + sessionId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", searchTime=" + searchTime +
                ", lastAccessed=" + lastAccessed +
                '}';
    }
}