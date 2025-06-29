package com.example.weather.entity;

import java.util.List;

public class HealthActivityData {

    private List<HealthIndex> healthIndices;
    private List<ActivityRecommendation> activityRecommendations;
    private String overallHealthRating;

    // Constructors
    public HealthActivityData() {}

    // Getters and Setters
    public List<HealthIndex> getHealthIndices() { return healthIndices; }
    public void setHealthIndices(List<HealthIndex> healthIndices) { this.healthIndices = healthIndices; }

    public List<ActivityRecommendation> getActivityRecommendations() { return activityRecommendations; }
    public void setActivityRecommendations(List<ActivityRecommendation> activityRecommendations) { this.activityRecommendations = activityRecommendations; }

    public String getOverallHealthRating() { return overallHealthRating; }
    public void setOverallHealthRating(String overallHealthRating) { this.overallHealthRating = overallHealthRating; }

    // Inner classes
    public static class HealthIndex {
        private String name;
        private Integer value;
        private String category;
        private String description;
        private String recommendation;

        public HealthIndex() {}

        public HealthIndex(String name, Integer value, String category, String description, String recommendation) {
            this.name = name;
            this.value = value;
            this.category = category;
            this.description = description;
            this.recommendation = recommendation;
        }

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Integer getValue() { return value; }
        public void setValue(Integer value) { this.value = value; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }

    public static class ActivityRecommendation {
        private String activity;
        private String suitability;
        private String recommendation;
        private String icon;

        public ActivityRecommendation() {}

        public ActivityRecommendation(String activity, String suitability, String recommendation, String icon) {
            this.activity = activity;
            this.suitability = suitability;
            this.recommendation = recommendation;
            this.icon = icon;
        }

        // Getters and Setters
        public String getActivity() { return activity; }
        public void setActivity(String activity) { this.activity = activity; }

        public String getSuitability() { return suitability; }
        public void setSuitability(String suitability) { this.suitability = suitability; }

        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }
}
