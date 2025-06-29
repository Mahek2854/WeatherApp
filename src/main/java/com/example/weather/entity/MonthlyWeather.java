package com.example.weather.entity;

import java.util.List;

public class MonthlyWeather {

    private String month;
    private Integer year;
    private List<MonthlyDay> dailyData;
    private MonthlyStats monthlyStats;

    // Constructors
    public MonthlyWeather() {}

    public MonthlyWeather(String month, Integer year) {
        this.month = month;
        this.year = year;
    }

    // Getters and Setters
    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public List<MonthlyDay> getDailyData() { return dailyData; }
    public void setDailyData(List<MonthlyDay> dailyData) { this.dailyData = dailyData; }

    public MonthlyStats getMonthlyStats() { return monthlyStats; }
    public void setMonthlyStats(MonthlyStats monthlyStats) { this.monthlyStats = monthlyStats; }

    // Inner classes
    public static class MonthlyDay {
        private Integer day;
        private Integer highTemp;
        private Integer lowTemp;
        private String weatherIcon;
        private String weatherDescription;
        private Integer precipitationChance;

        public MonthlyDay() {}

        // Getters and Setters
        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }

        public Integer getHighTemp() { return highTemp; }
        public void setHighTemp(Integer highTemp) { this.highTemp = highTemp; }

        public Integer getLowTemp() { return lowTemp; }
        public void setLowTemp(Integer lowTemp) { this.lowTemp = lowTemp; }

        public String getWeatherIcon() { return weatherIcon; }
        public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }

        public String getWeatherDescription() { return weatherDescription; }
        public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }

        public Integer getPrecipitationChance() { return precipitationChance; }
        public void setPrecipitationChance(Integer precipitationChance) { this.precipitationChance = precipitationChance; }
    }

    public static class MonthlyStats {
        private Integer avgHighTemp;
        private Integer avgLowTemp;
        private Integer totalRainfall;
        private Integer rainyDays;
        private Integer sunnyDays;

        public MonthlyStats() {}

        // Getters and Setters
        public Integer getAvgHighTemp() { return avgHighTemp; }
        public void setAvgHighTemp(Integer avgHighTemp) { this.avgHighTemp = avgHighTemp; }

        public Integer getAvgLowTemp() { return avgLowTemp; }
        public void setAvgLowTemp(Integer avgLowTemp) { this.avgLowTemp = avgLowTemp; }

        public Integer getTotalRainfall() { return totalRainfall; }
        public void setTotalRainfall(Integer totalRainfall) { this.totalRainfall = totalRainfall; }

        public Integer getRainyDays() { return rainyDays; }
        public void setRainyDays(Integer rainyDays) { this.rainyDays = rainyDays; }

        public Integer getSunnyDays() { return sunnyDays; }
        public void setSunnyDays(Integer sunnyDays) { this.sunnyDays = sunnyDays; }
    }
}
