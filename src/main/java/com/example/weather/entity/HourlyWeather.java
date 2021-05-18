package com.example.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class HourlyWeather {

    private Integer time;
    private double temperature;
    private String weatherIcon;
    private String timeValue;
    private double windSpeed;
    private Integer windDegree;
    private double rain;
    private double pop;

    private Integer tempValue;
    private String iconLink;
    private Integer windSpeedValue;
    private Integer rainValue;
    private Integer rainPossibility;

    public Integer getTime() {
        return time;
    }

    @JsonProperty("dt")
    public void setTime(Integer time) {
        this.time = time;
    }

    public double getTemperature() {
        return temperature;
    }

    @JsonProperty("temp")
    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getWeatherIcon() {
        return weatherIcon;
    }

    @JsonProperty("icon")
    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
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

    public double getRain() {
        return rain;
    }

    @JsonProperty("1h")
    public void setRain(double rain) {
        this.rain = rain;
    }

    public double getPop() {
        return pop;
    }

    @JsonProperty("pop")
    public void setPop(double pop) {
        this.pop = pop;
    }

    @JsonProperty("hourly")
    public void setHourlyWeather(List<Map<String, Object>> hourlyEntries) {
        Map<String, Object> hourly = hourlyEntries.get(1);
        setWeather((List<Map<String, Object>>) hourly.get("weather"));
        setRainValue((Map<String, Object>) hourly.get("rain"));
        setTemperature((double) hourly.get("temp"));
        setWindSpeed((double) hourly.get("wind_speed"));
        setWindDegree((Integer) hourly.get("wind_deg"));
        setTime((Integer) hourly.get("dt"));
        setPop((double) hourly.get("pop"));
    }

    @JsonProperty("weather")
    public void setWeather(List<Map<String, Object>> weatherEntries) {
        Map<String, Object> weather = weatherEntries.get(0);
        setWeatherIcon((String) weather.get("icon"));
    }

    @JsonProperty("rain")
    public void setRainValue(Map<String, Object> rainn) {
        setRain((double) rainn.get("1h"));
    }

    public String getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(String timeValue) {
        this.timeValue = timeValue;
    }

    public Integer getTempValue() {
        return tempValue = Math.toIntExact(Math.round(temperature));
    }

    public void setTempValue(Integer tempValue) {
        this.tempValue = tempValue;
    }

    public String getIconLink() {
        return iconLink = "http://openweathermap.org/img/wn/" + weatherIcon + ".png";
    }

    public void setIconLink(String iconLink) {
        this.iconLink = iconLink;
    }

    public Integer getWindSpeedValue() {
        return windSpeedValue = Math.toIntExact(Math.round(windSpeed));
    }

    public void setWindSpeedValue(Integer windSpeedValue) {
        this.windSpeedValue = windSpeedValue;
    }

    public Integer getRainValue() {
        return rainValue = Math.toIntExact(Math.round(rain));
    }

    public void setRainValue(Integer rainValue) {
        this.rainValue = rainValue;
    }

    public Integer getRainPossibility() {
        return rainPossibility = Math.toIntExact(Math.round(pop));
    }

    public void setRainPossibility(Integer rainPossibility) {
        this.rainPossibility = rainPossibility;
    }
}
