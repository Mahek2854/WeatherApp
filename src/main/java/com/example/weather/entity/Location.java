package com.example.weather.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.SimpleTimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {

    public String cityName;
    public String country;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, hh:mm a");
    Date date = new Date();
    String currentDateTime = dateFormat.format(date);

    public String getCityName() {
        return cityName;
    }

    @JsonProperty("cityName")
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    @JsonProperty("countryName")
    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrentDateTime() {
        return currentDateTime;
    }

    public void setCurrentDateTime(String currentDateTime) {
        this.currentDateTime = currentDateTime;
    }
}
