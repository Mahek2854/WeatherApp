package com.example.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather {

    private String weatherMain;
    private String weatherDescription;
    private String weatherIcon;
    private double temperature;
    private double feelsLike;
    private Integer pressure;
    private Integer humidity;
    private double uvi;
    private Integer visibility;
    private double windSpeed;
    private Integer windDegree;
    private Integer sunrise;
    private Integer sunset;

    private Integer tempValue;
    private Integer feelsLikeValue;
    private Integer windSpeedValue;
    private String sunsetValue;
    private String sunriseValue;

    @Bean
    public CurrentWeather weather() {
        return new CurrentWeather();
    }

    public CurrentWeather() {
        super();
    }

    public CurrentWeather(CurrentWeather weather) {
    }

    public String getWeatherMain() {
        return weatherMain;
    }

    public void setWeatherMain(String weatherMain) {
        this.weatherMain = weatherMain;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }

    public void setWeatherDescription(String weatherDescription) {
        this.weatherDescription = weatherDescription;
    }

    public String getWeatherIcon() {
        return weatherIcon = "http://openweathermap.org/img/wn/" + weatherIcon + "@4x.png";
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    @JsonProperty("weather")
    public void setWeather(List<Map<String, Object>> weatherEntries) {
        Map<String, Object> weather = weatherEntries.get(0);
        setWeatherMain((String) weather.get("main"));
        setWeatherDescription((String) weather.get("description"));
        setWeatherIcon((String) weather.get("icon"));
    }

    public double getTemperature() {
        return temperature;
    }

    @JsonProperty("temp")
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    @JsonProperty("feels_like")
    public void setFeelsLike(double feelsLike) {
        this.feelsLike = feelsLike;
    }

    public Integer getPressure() {
        return pressure;
    }

    @JsonProperty("pressure")
    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    public Integer getHumidity() {
        return humidity;
    }

    @JsonProperty("humidity")
    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    public double getUvi() {
        return uvi;
    }

    @JsonProperty("uvi")
    public void setUvi(double uvi) {
        this.uvi = uvi;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    @JsonProperty("wind_speed")
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Integer getWindDegree() {
        return windDegree;
    }

    @JsonProperty("wind_deg")
    public void setWindDegree(Integer windDegree) {
        this.windDegree = windDegree;
    }

    public Integer getSunrise() {
        return sunrise;
    }

    @JsonProperty("sunrise")
    public void setSunrise(Integer sunrise) {
        this.sunrise = sunrise;
    }

    public Integer getSunset() {
        return sunset;
    }

    @JsonProperty("sunset")
    public void setSunset(Integer sunset) {
        this.sunset = sunset;
    }

    public Integer getVisibility() {
        return visibility;
    }

    @JsonProperty("visibility")
    public void setVisibility(Integer visibility) {
        this.visibility = visibility;
    }

    @JsonProperty("current")
    public void setMain(Map<String, Object> current) {
        setTemperature((double) current.get("temp"));
        setFeelsLike((double) current.get("feels_like"));
        setPressure((Integer) current.get("pressure"));
        setHumidity((Integer) current.get("humidity"));
        setUvi((Integer) current.get("uvi"));
        setSunrise((Integer) current.get("sunrise"));
        setSunset((Integer) current.get("sunset"));;
        setVisibility((Integer) current.get("visibility"));
        setWindSpeed((double) current.get("wind_speed"));
        setWindDegree((Integer) current.get("wind_deg"));
        setWeather((List<Map<String, Object>>) current.get("weather"));
    }

    public Integer getTempValue() {
        return tempValue = Math.toIntExact(Math.round(temperature));
    }

    public void setTempValue(Integer tempValue) {
        this.tempValue = tempValue;
    }

    public Integer getFeelsLikeValue() {
        return feelsLikeValue =  Math.toIntExact(Math.round(feelsLike));
    }

    public void setFeelsLikeValue(Integer feelsLikeValue) {
        this.feelsLikeValue = feelsLikeValue;
    }

    public String getSunsetValue() {
        return sunsetValue;
    }

    public void setSunsetValue(String sunsetValue) {
        this.sunsetValue = sunsetValue;
    }

    public String getSunriseValue() {
        return sunriseValue;
    }

    public void setSunriseValue(String sunriseValue) {
        this.sunriseValue = sunriseValue;
    }

    public Integer getWindSpeedValue() {
        return windSpeedValue = Math.toIntExact(Math.round(windSpeed));
    }

    public void setWindSpeedValue(Integer windSpeedValue) {
        this.windSpeedValue = windSpeedValue;
    }

}
