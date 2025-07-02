package com.example.weather.service;

import com.example.weather.entity.WeatherData;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CommuteRecommendationService {

    public List<CommuteRecommendation> generateRecommendations(WeatherData weather, String origin, String destination) {
        List<CommuteRecommendation> recommendations = new ArrayList<>();

        // Generate recommendations for different time slots
        LocalTime[] timeSlots = {
                LocalTime.of(7, 0),   // 7:00 AM
                LocalTime.of(7, 30),  // 7:30 AM
                LocalTime.of(8, 0),   // 8:00 AM
                LocalTime.of(8, 30),  // 8:30 AM
                LocalTime.of(9, 0),   // 9:00 AM
                LocalTime.of(17, 0),  // 5:00 PM
                LocalTime.of(17, 30), // 5:30 PM
                LocalTime.of(18, 0)   // 6:00 PM
        };

        for (LocalTime time : timeSlots) {
            CommuteRecommendation rec = calculateRecommendation(time, weather);
            recommendations.add(rec);
        }

        // Sort by score (best first)
        recommendations.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        return recommendations;
    }

    private CommuteRecommendation calculateRecommendation(LocalTime time, WeatherData weather) {
        double score = 100.0; // Start with perfect score
        String riskLevel = "low";
        List<String> factors = new ArrayList<>();

        // Weather Impact Analysis
        score = applyWeatherFactors(score, weather, factors);

        // Traffic Pattern Analysis (based on time)
        score = applyTrafficFactors(score, time, factors);

        // Determine risk level
        if (score >= 80) {
            riskLevel = "low";
        } else if (score >= 60) {
            riskLevel = "medium";
        } else {
            riskLevel = "high";
        }

        return new CommuteRecommendation(
                formatTimeSlot(time),
                riskLevel,
                score,
                generateDescription(factors, score),
                factors
        );
    }

    private double applyWeatherFactors(double score, WeatherData weather, List<String> factors) {
        if (weather == null) return score;

        // Rain impact
        if (weather.getWeatherMain() != null && weather.getWeatherMain().toLowerCase().contains("rain")) {
            score -= 20;
            factors.add("Rain expected - allow extra time");
        }

        // Snow impact
        if (weather.getWeatherMain() != null && weather.getWeatherMain().toLowerCase().contains("snow")) {
            score -= 30;
            factors.add("Snow conditions - significant delays possible");
        }

        // Wind impact
        if (weather.getWindSpeed() != null && weather.getWindSpeed() > 15) {
            score -= 10;
            factors.add("High winds - use caution");
        }

        // Visibility impact
        if (weather.getVisibility() != null && weather.getVisibility() < 5) {
            score -= 15;
            factors.add("Low visibility - reduced speeds");
        }

        // Temperature extremes
        if (weather.getTemperature() != null) {
            if (weather.getTemperature() < -10) {
                score -= 15;
                factors.add("Extreme cold - potential ice");
            } else if (weather.getTemperature() > 35) {
                score -= 10;
                factors.add("Extreme heat - vehicle stress");
            }
        }

        return Math.max(score, 0);
    }

    private double applyTrafficFactors(double score, LocalTime time, List<String> factors) {
        int hour = time.getHour();

        // Morning rush hour (7-9 AM)
        if (hour >= 7 && hour <= 9) {
            score -= 25;
            factors.add("Morning rush hour - heavy traffic");
        }

        // Evening rush hour (5-7 PM)
        if (hour >= 17 && hour <= 19) {
            score -= 30;
            factors.add("Evening rush hour - peak traffic");
        }

        // Optimal times (before 7 AM or 9-4 PM)
        if (hour < 7 || (hour >= 9 && hour <= 16)) {
            score += 10;
            factors.add("Off-peak hours - lighter traffic");
        }

        return Math.max(score, 0);
    }

    private String formatTimeSlot(LocalTime time) {
        LocalTime endTime = time.plusMinutes(30);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return time.format(formatter) + " - " + endTime.format(formatter);
    }

    private String generateDescription(List<String> factors, double score) {
        if (score >= 80) {
            return "Optimal travel conditions - " + String.join(", ", factors);
        } else if (score >= 60) {
            return "Moderate conditions - " + String.join(", ", factors);
        } else {
            return "Challenging conditions - " + String.join(", ", factors);
        }
    }

    // Inner class for recommendation data
    public static class CommuteRecommendation {
        private String timeSlot;
        private String riskLevel;
        private double score;
        private String description;
        private List<String> factors;

        public CommuteRecommendation(String timeSlot, String riskLevel, double score, String description, List<String> factors) {
            this.timeSlot = timeSlot;
            this.riskLevel = riskLevel;
            this.score = score;
            this.description = description;
            this.factors = factors;
        }

        // Getters
        public String getTimeSlot() { return timeSlot; }
        public String getRiskLevel() { return riskLevel; }
        public double getScore() { return score; }
        public String getDescription() { return description; }
        public List<String> getFactors() { return factors; }
    }
}
