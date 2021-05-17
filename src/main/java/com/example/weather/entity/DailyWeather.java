package com.example.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyWeather {

    private double morningTemperature;
    private double dayTemperature;
    private double nightTemperature;

    private Integer morningTemp;
    private Integer dayTemp;
    private Integer nightTemp;

    public double getMorningTemperature() {
        return morningTemperature;
    }

    @JsonProperty("morn")
    public void setMorningTemperature(double morningTemperature) {
        this.morningTemperature = morningTemperature;
    }

    public double getDayTemperature() {
        return dayTemperature;
    }

    @JsonProperty("day")
    public void setDayTemperature(double dayTemperature) {
        this.dayTemperature = dayTemperature;
    }

    public double getNightTemperature() {
        return nightTemperature;
    }

    @JsonProperty("night")
    public void setNightTemperature(double nightTemperature) {
        this.nightTemperature = nightTemperature;
    }

    @JsonProperty("daily")
    public void setDailyWeather(List<Map<String, Object>> dailyEntries) {
        Map<String, Object> daily = dailyEntries.get(0);
        setDaily((Map<String, Object>) daily.get("temp"));
    }

    @JsonProperty("temp")
    public void setDaily(Map<String, Object> daily) {
        setDayTemperature((double) daily.get("day"));
        setMorningTemperature((double) daily.get("morn"));
        setNightTemperature((double) daily.get("night"));
    }

    public Integer getMorningTemp() {
        return morningTemp = Math.toIntExact(Math.round(morningTemperature));
    }

    public void setMorningTemp(Integer morningTemp) {
        this.morningTemp = morningTemp;
    }

    public Integer getDayTemp() {
        return dayTemp = Math.toIntExact(Math.round(dayTemperature));
    }

    public void setDayTemp(Integer dayTemp) {
        this.dayTemp = dayTemp;
    }

    public Integer getNightTemp() {
        return nightTemp = Math.toIntExact(Math.round(nightTemperature));
    }

    public void setNightTemp(Integer nightTemp) {
        this.nightTemp = nightTemp;
    }
}
