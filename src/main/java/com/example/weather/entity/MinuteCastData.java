package com.example.weather.entity;

import java.util.List;

public class MinuteCastData {

    private String summary;
    private List<MinuteData> minutelyData;
    private String precipitationType;
    private Integer precipitationIntensity;
    private String precipitationSummary;

    // Constructors
    public MinuteCastData() {}

    // Getters and Setters
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public List<MinuteData> getMinutelyData() { return minutelyData; }
    public void setMinutelyData(List<MinuteData> minutelyData) { this.minutelyData = minutelyData; }

    public String getPrecipitationType() { return precipitationType; }
    public void setPrecipitationType(String precipitationType) { this.precipitationType = precipitationType; }

    public Integer getPrecipitationIntensity() { return precipitationIntensity; }
    public void setPrecipitationIntensity(Integer precipitationIntensity) { this.precipitationIntensity = precipitationIntensity; }

    public String getPrecipitationSummary() { return precipitationSummary; }
    public void setPrecipitationSummary(String precipitationSummary) { this.precipitationSummary = precipitationSummary; }

    // Inner class for minute-by-minute data
    public static class MinuteData {
        private Integer minute;
        private Double precipitationProbability;
        private Double precipitationIntensity;
        private String precipitationType;

        public MinuteData() {}

        public MinuteData(Integer minute, Double precipitationProbability,
                          Double precipitationIntensity, String precipitationType) {
            this.minute = minute;
            this.precipitationProbability = precipitationProbability;
            this.precipitationIntensity = precipitationIntensity;
            this.precipitationType = precipitationType;
        }

        // Getters and Setters
        public Integer getMinute() { return minute; }
        public void setMinute(Integer minute) { this.minute = minute; }

        public Double getPrecipitationProbability() { return precipitationProbability; }
        public void setPrecipitationProbability(Double precipitationProbability) { this.precipitationProbability = precipitationProbability; }

        public Double getPrecipitationIntensity() { return precipitationIntensity; }
        public void setPrecipitationIntensity(Double precipitationIntensity) { this.precipitationIntensity = precipitationIntensity; }

        public String getPrecipitationType() { return precipitationType; }
        public void setPrecipitationType(String precipitationType) { this.precipitationType = precipitationType; }
    }
}
