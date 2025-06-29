package com.example.weather.entity;

import com.example.weather.service.WeatherService.HourlyForecast;
import com.example.weather.service.WeatherService.DailyForecast;
import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "weather_data")
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;
    private String country;
    private Double temperature;
    private Double feelsLike;
    private String description;
    private String weatherMain;
    private String weatherIcon;
    private Integer humidity;
    private Double pressure;
    private Double windSpeed;
    private Integer windDirection;
    private Double visibility;
    private Double uvIndex;
    private String sunrise;
    private String sunset;
    private Double latitude;
    private Double longitude;

    @Transient
    private List<HourlyForecast> hourlyForecast;

    @Transient
    private List<DailyForecast> dailyForecast;

    // Default constructor
    public WeatherData() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(Double feelsLike) { this.feelsLike = feelsLike; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getWeatherMain() { return weatherMain; }
    public void setWeatherMain(String weatherMain) { this.weatherMain = weatherMain; }

    public String getWeatherIcon() { return weatherIcon; }
    public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }

    public Integer getHumidity() { return humidity; }
    public void setHumidity(Integer humidity) { this.humidity = humidity; }

    public Double getPressure() { return pressure; }
    public void setPressure(Double pressure) { this.pressure = pressure; }

    public Double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

    public Integer getWindDirection() { return windDirection; }
    public void setWindDirection(Integer windDirection) { this.windDirection = windDirection; }

    public Double getVisibility() { return visibility; }
    public void setVisibility(Double visibility) { this.visibility = visibility; }

    public Double getUvIndex() { return uvIndex; }
    public void setUvIndex(Double uvIndex) { this.uvIndex = uvIndex; }

    public String getSunrise() { return sunrise; }
    public void setSunrise(String sunrise) { this.sunrise = sunrise; }

    public String getSunset() { return sunset; }
    public void setSunset(String sunset) { this.sunset = sunset; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public List<HourlyForecast> getHourlyForecast() { return hourlyForecast; }
    public void setHourlyForecast(List<HourlyForecast> hourlyForecast) { this.hourlyForecast = hourlyForecast; }

    public List<DailyForecast> getDailyForecast() { return dailyForecast; }
    public void setDailyForecast(List<DailyForecast> dailyForecast) { this.dailyForecast = dailyForecast; }
}