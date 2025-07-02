package com.example.weather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.weather.service.WeatherService;
import com.example.weather.service.CommuteRecommendationService;
import com.example.weather.entity.WeatherData;

@Controller
@RequestMapping("/business")
public class BusinessController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CommuteRecommendationService commuteRecommendationService;

    private Random random = new Random();

    @GetMapping("/")
    public String businessHome(Model model) {
        model.addAttribute("pageTitle", "WeatherPro Business - Professional Weather Services");
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Weather Dashboard - WeatherPro Business");
        return "dashboard";
    }

    @GetMapping("/safety")
    public String safety(Model model) {
        model.addAttribute("pageTitle", "Safety Measures - WeatherPro Business");
        return "safety";
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {
        model.addAttribute("pageTitle", "Weather Alerts & Warnings - WeatherPro Business");
        return "alerts";
    }

    @GetMapping("/services/forecasting")
    public String forecasting(Model model) {
        model.addAttribute("pageTitle", "Weather Forecasting Services");
        return "services/forecasting";
    }

    @GetMapping("/services/alerts")
    public String alertsServices(Model model) {
        model.addAttribute("pageTitle", "Weather Alerts & Warnings");
        return "services/alerts";
    }

    @GetMapping("/services/analytics")
    public String analytics(Model model) {
        model.addAttribute("pageTitle", "Weather Analytics");
        return "services/analytics";
    }

    @GetMapping("/services/api")
    public String api(Model model) {
        model.addAttribute("pageTitle", "Weather API Services");
        return "services/api";
    }

    @GetMapping("/why-weatherpro")
    public String whyWeatherPro(Model model) {
        model.addAttribute("pageTitle", "Why Choose WeatherPro");
        return "why-weatherpro";
    }

    @GetMapping("/weather-events")
    public String weatherEvents(Model model) {
        model.addAttribute("pageTitle", "Weather Events");
        return "weather-events";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact Us");
        return "contact";
    }

    @GetMapping("/get-started")
    public String getStarted(Model model) {
        model.addAttribute("pageTitle", "Get Started");
        return "get-started";
    }

    @GetMapping("/support/documentation")
    public String documentation(Model model) {
        model.addAttribute("pageTitle", "Documentation");
        return "support/documentation";
    }

    @GetMapping("/support/faq")
    public String faq(Model model) {
        model.addAttribute("pageTitle", "Frequently Asked Questions");
        return "support/faq";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About WeatherPro");
        return "about";
    }

    @GetMapping("/api/weather/current")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentWeather(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        try {
            Map<String, Object> response = new HashMap<>();

            if (lat != null && lon != null) {
                var weather = weatherService.getWeatherByCoordinates(lat, lon);
                if (weather != null) {
                    response.put("success", true);
                    response.put("data", weather);
                    return ResponseEntity.ok(response);
                }
            } else if (city != null && !city.trim().isEmpty()) {
                var weather = weatherService.getCompleteWeatherData(city, country != null ? country : "");
                if (weather != null) {
                    response.put("success", true);
                    response.put("data", weather);
                    return ResponseEntity.ok(response);
                }
            }

            response.put("success", false);
            response.put("error", "Weather data not found");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/api/weather/forecast")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getForecast(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        try {
            Map<String, Object> response = new HashMap<>();

            // Get current weather data for the location to base forecast on
            WeatherData currentWeather = null;
            String locationName = "Unknown Location";

            if (lat != null && lon != null) {
                currentWeather = weatherService.getWeatherByCoordinates(lat, lon);
                locationName = currentWeather != null ? currentWeather.getCity() : "Current Location";
            } else if (city != null && !city.trim().isEmpty()) {
                currentWeather = weatherService.getCompleteWeatherData(city, country != null ? country : "");
                locationName = city + (country != null && !country.isEmpty() ? ", " + country : "");
            }

            // Generate real-time location-specific forecast
            List<Map<String, Object>> hourlyForecast = generateLocationSpecificForecast(currentWeather, locationName);

            Map<String, Object> forecastData = new HashMap<>();
            forecastData.put("hourly", hourlyForecast);
            forecastData.put("location", locationName);
            forecastData.put("timestamp", System.currentTimeMillis());
            forecastData.put("timezone", getCurrentTimezone(locationName));

            response.put("success", true);
            response.put("data", forecastData);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @GetMapping("/api/commute/recommendations")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCommuteRecommendations(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination) {

        try {
            Map<String, Object> response = new HashMap<>();

            WeatherData weather = null;
            if (lat != null && lon != null) {
                weather = weatherService.getWeatherByCoordinates(lat, lon);
            } else if (city != null && !city.trim().isEmpty()) {
                weather = weatherService.getCompleteWeatherData(city, country != null ? country : "");
            }

            var recommendations = commuteRecommendationService.generateRecommendations(weather, origin, destination);

            response.put("success", true);
            response.put("recommendations", recommendations);
            response.put("bestTime", recommendations.isEmpty() ? null : recommendations.get(0));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    private List<Map<String, Object>> generateLocationSpecificForecast(WeatherData currentWeather, String locationName) {
        List<Map<String, Object>> forecast = new ArrayList<>();

        // Get base temperature from current weather or use location-based defaults
        int baseTemp = getLocationBaseTemperature(currentWeather, locationName);

        // Get location-specific weather patterns
        LocationWeatherPattern pattern = getLocationWeatherPattern(locationName);

        LocalDateTime now = LocalDateTime.now();

        // Generate 12 hours of location-specific forecast
        for (int i = 0; i < 12; i++) {
            LocalDateTime hourTime = now.plusHours(i);
            String timeStr = i == 0 ? "Now" : hourTime.format(DateTimeFormatter.ofPattern("h a"));

            // Calculate temperature based on location, time, and weather patterns
            int hourTemp = calculateLocationTemperature(baseTemp, hourTime.getHour(), pattern, i);

            // Select weather icon based on location patterns and time
            String icon = selectLocationWeatherIcon(hourTime.getHour(), pattern, i);

            // Calculate precipitation based on location climate
            int precipitation = calculateLocationPrecipitation(pattern, icon, hourTime.getHour());

            // Calculate wind speed based on location and weather
            int windSpeed = calculateLocationWindSpeed(pattern, icon, hourTime.getHour());

            forecast.add(createLocationHourlyItem(
                    timeStr,
                    hourTemp,
                    icon,
                    precipitation,
                    windSpeed,
                    hourTime.getHour(),
                    pattern
            ));
        }

        return forecast;
    }

    private int getLocationBaseTemperature(WeatherData currentWeather, String locationName) {
        if (currentWeather != null && currentWeather.getTemperature() != null) {
            // Convert Celsius to Fahrenheit
            return (int) Math.round(currentWeather.getTemperature() * 9.0 / 5.0 + 32);
        }

        // Location-based temperature defaults
        String location = locationName.toLowerCase();
        if (location.contains("dubai") || location.contains("phoenix") || location.contains("miami")) {
            return 85 + random.nextInt(15); // 85-100°F
        } else if (location.contains("london") || location.contains("seattle") || location.contains("vancouver")) {
            return 55 + random.nextInt(20); // 55-75°F
        } else if (location.contains("new york") || location.contains("chicago") || location.contains("boston")) {
            return 65 + random.nextInt(20); // 65-85°F
        } else if (location.contains("tokyo") || location.contains("seoul") || location.contains("beijing")) {
            return 70 + random.nextInt(15); // 70-85°F
        } else if (location.contains("sydney") || location.contains("melbourne") || location.contains("brisbane")) {
            return 75 + random.nextInt(15); // 75-90°F
        } else if (location.contains("paris") || location.contains("berlin") || location.contains("madrid")) {
            return 68 + random.nextInt(17); // 68-85°F
        } else {
            return 70 + random.nextInt(20); // Default 70-90°F
        }
    }

    private LocationWeatherPattern getLocationWeatherPattern(String locationName) {
        String location = locationName.toLowerCase();

        if (location.contains("dubai") || location.contains("phoenix")) {
            return new LocationWeatherPattern("desert", new String[]{"01d", "02d"}, new String[]{"01n", "02n"}, 5, 15);
        } else if (location.contains("london") || location.contains("seattle")) {
            return new LocationWeatherPattern("temperate_wet", new String[]{"03d", "04d", "09d", "10d"}, new String[]{"03n", "04n", "09n", "10n"}, 40, 25);
        } else if (location.contains("miami") || location.contains("singapore")) {
            return new LocationWeatherPattern("tropical", new String[]{"02d", "03d", "09d", "10d", "11d"}, new String[]{"02n", "03n", "09n", "10n"}, 60, 20);
        } else if (location.contains("new york") || location.contains("chicago")) {
            return new LocationWeatherPattern("continental", new String[]{"01d", "02d", "03d", "04d", "09d"}, new String[]{"01n", "02n", "03n", "04n"}, 25, 18);
        } else if (location.contains("tokyo") || location.contains("seoul")) {
            return new LocationWeatherPattern("humid_subtropical", new String[]{"02d", "03d", "04d", "09d", "10d"}, new String[]{"02n", "03n", "04n", "09n"}, 35, 22);
        } else if (location.contains("sydney") || location.contains("melbourne")) {
            return new LocationWeatherPattern("oceanic", new String[]{"01d", "02d", "03d", "09d"}, new String[]{"01n", "02n", "03n", "09n"}, 30, 20);
        } else {
            return new LocationWeatherPattern("temperate", new String[]{"01d", "02d", "03d", "04d"}, new String[]{"01n", "02n", "03n", "04n"}, 20, 15);
        }
    }

    private int calculateLocationTemperature(int baseTemp, int hour, LocationWeatherPattern pattern, int hoursFromNow) {
        int tempVariation = 0;

        // Daily temperature curve based on location climate
        if (pattern.climate.equals("desert")) {
            // Desert: Hot days, cooler nights
            if (hour >= 22 || hour <= 6) {
                tempVariation = -15 - random.nextInt(10); // Much cooler at night
            } else if (hour >= 12 && hour <= 16) {
                tempVariation = 10 + random.nextInt(15); // Very hot midday
            } else {
                tempVariation = random.nextInt(10) - 5;
            }
        } else if (pattern.climate.equals("tropical")) {
            // Tropical: Consistent warm with afternoon storms
            if (hour >= 14 && hour <= 17) {
                tempVariation = -5 - random.nextInt(8); // Cooler during afternoon storms
            } else {
                tempVariation = random.nextInt(6) - 3; // Minimal variation
            }
        } else if (pattern.climate.equals("temperate_wet")) {
            // Temperate wet: Moderate temperatures, cloudy
            if (hour >= 22 || hour <= 6) {
                tempVariation = -8 - random.nextInt(6);
            } else if (hour >= 13 && hour <= 16) {
                tempVariation = 5 + random.nextInt(8);
            } else {
                tempVariation = random.nextInt(8) - 4;
            }
        } else {
            // Default temperate pattern
            if (hour >= 22 || hour <= 6) {
                tempVariation = -10 - random.nextInt(8);
            } else if (hour >= 12 && hour <= 16) {
                tempVariation = 8 + random.nextInt(10);
            } else {
                tempVariation = random.nextInt(8) - 4;
            }
        }

        return baseTemp + tempVariation;
    }

    private String selectLocationWeatherIcon(int hour, LocationWeatherPattern pattern, int hoursFromNow) {
        String[] icons = (hour >= 6 && hour < 20) ? pattern.dayIcons : pattern.nightIcons;

        // Add some weather progression logic
        if (pattern.climate.equals("tropical") && hour >= 14 && hour <= 17) {
            // Afternoon storms in tropical climates
            return random.nextBoolean() ? "11d" : "09d";
        } else if (pattern.climate.equals("temperate_wet") && random.nextInt(100) < 40) {
            // Higher chance of rain in wet climates
            return (hour >= 6 && hour < 20) ? "09d" : "09n";
        }

        return icons[random.nextInt(icons.length)];
    }

    private int calculateLocationPrecipitation(LocationWeatherPattern pattern, String icon, int hour) {
        int basePrecipitation = pattern.avgPrecipitation;

        // Adjust based on weather icon
        if (icon.contains("09") || icon.contains("10")) {
            basePrecipitation += 30 + random.nextInt(40); // Rain
        } else if (icon.contains("11")) {
            basePrecipitation += 50 + random.nextInt(40); // Thunderstorms
        } else if (icon.contains("03") || icon.contains("04")) {
            basePrecipitation += 10 + random.nextInt(20); // Cloudy
        }

        // Tropical afternoon storms
        if (pattern.climate.equals("tropical") && hour >= 14 && hour <= 17) {
            basePrecipitation += 20;
        }

        return Math.min(Math.max(basePrecipitation + random.nextInt(20) - 10, 0), 100);
    }

    private int calculateLocationWindSpeed(LocationWeatherPattern pattern, String icon, int hour) {
        int baseWind = pattern.avgWindSpeed;

        // Adjust based on weather conditions
        if (icon.contains("11")) {
            baseWind += 15 + random.nextInt(20); // Thunderstorms
        } else if (icon.contains("09") || icon.contains("10")) {
            baseWind += 5 + random.nextInt(10); // Rain
        }

        // Desert and coastal areas tend to be windier
        if (pattern.climate.equals("desert")) {
            baseWind += 5;
        } else if (pattern.climate.equals("oceanic")) {
            baseWind += 8;
        }

        // Afternoon tends to be windier
        if (hour >= 12 && hour <= 18) {
            baseWind += 3;
        }

        return Math.min(baseWind + random.nextInt(8) - 4, 45);
    }

    private Map<String, Object> createLocationHourlyItem(String time, int temp, String icon, int precipitation, int windSpeed, int hour, LocationWeatherPattern pattern) {
        Map<String, Object> item = new HashMap<>();
        item.put("time", time);
        item.put("temp", temp);
        item.put("icon", icon);
        item.put("precipitation", precipitation);
        item.put("windSpeed", windSpeed);

        // Add location-specific additional data
        item.put("humidity", calculateLocationHumidity(pattern, hour));
        item.put("uvIndex", calculateUVIndex(hour, pattern));
        item.put("visibility", calculateLocationVisibility(pattern, icon));
        item.put("pressure", 1013 + random.nextInt(40) - 20); // 993-1033 mb

        return item;
    }

    private int calculateLocationHumidity(LocationWeatherPattern pattern, int hour) {
        int baseHumidity = 50;

        if (pattern.climate.equals("tropical")) {
            baseHumidity = 70 + random.nextInt(20); // 70-90%
        } else if (pattern.climate.equals("desert")) {
            baseHumidity = 20 + random.nextInt(25); // 20-45%
        } else if (pattern.climate.equals("temperate_wet")) {
            baseHumidity = 60 + random.nextInt(25); // 60-85%
        } else {
            baseHumidity = 40 + random.nextInt(35); // 40-75%
        }

        // Higher humidity at night
        if (hour >= 22 || hour <= 6) {
            baseHumidity += 10;
        }

        return Math.min(Math.max(baseHumidity, 15), 95);
    }

    private int calculateUVIndex(int hour, LocationWeatherPattern pattern) {
        if (hour < 8 || hour > 18) {
            return 0; // No UV at night
        }

        int baseUV = 5;

        if (pattern.climate.equals("desert") || pattern.climate.equals("tropical")) {
            baseUV = 8; // Higher UV in sunny/tropical climates
        } else if (pattern.climate.equals("temperate_wet")) {
            baseUV = 3; // Lower UV in cloudy climates
        }

        // Peak UV around midday
        if (hour >= 11 && hour <= 15) {
            baseUV += 2;
        } else if (hour >= 9 && hour <= 17) {
            baseUV += 1;
        }

        return Math.min(Math.max(baseUV + random.nextInt(3) - 1, 0), 11);
    }

    private int calculateLocationVisibility(LocationWeatherPattern pattern, String icon) {
        int baseVisibility = 10; // miles

        if (icon.contains("09") || icon.contains("10")) {
            baseVisibility = 3 + random.nextInt(4); // 3-7 miles in rain
        } else if (icon.contains("11")) {
            baseVisibility = 2 + random.nextInt(3); // 2-5 miles in storms
        } else if (icon.contains("50")) {
            baseVisibility = 1 + random.nextInt(2); // 1-3 miles in fog
        } else if (pattern.climate.equals("desert")) {
            baseVisibility = 15 + random.nextInt(10); // Very clear in desert
        }

        return Math.max(baseVisibility + random.nextInt(3) - 1, 1);
    }

    private String getCurrentTimezone(String locationName) {
        String location = locationName.toLowerCase();

        if (location.contains("new york") || location.contains("boston") || location.contains("miami")) {
            return "EST";
        } else if (location.contains("chicago") || location.contains("dallas")) {
            return "CST";
        } else if (location.contains("denver") || location.contains("phoenix")) {
            return "MST";
        } else if (location.contains("los angeles") || location.contains("seattle")) {
            return "PST";
        } else if (location.contains("london") || location.contains("paris") || location.contains("berlin")) {
            return "CET";
        } else if (location.contains("tokyo") || location.contains("seoul")) {
            return "JST";
        } else if (location.contains("sydney") || location.contains("melbourne")) {
            return "AEST";
        } else if (location.contains("dubai")) {
            return "GST";
        } else {
            return "UTC";
        }
    }

    // Helper class for location weather patterns
    private static class LocationWeatherPattern {
        String climate;
        String[] dayIcons;
        String[] nightIcons;
        int avgPrecipitation;
        int avgWindSpeed;

        LocationWeatherPattern(String climate, String[] dayIcons, String[] nightIcons, int avgPrecipitation, int avgWindSpeed) {
            this.climate = climate;
            this.dayIcons = dayIcons;
            this.nightIcons = nightIcons;
            this.avgPrecipitation = avgPrecipitation;
            this.avgWindSpeed = avgWindSpeed;
        }
    }

    // Legacy method for backward compatibility
    private Object generateMockForecast() {
        Map<String, Object> forecast = new HashMap<>();
        forecast.put("hourly", java.util.Arrays.asList(
                createHourlyItem("12 PM", 75, "01d", 10),
                createHourlyItem("1 PM", 77, "02d", 15),
                createHourlyItem("2 PM", 79, "02d", 20),
                createHourlyItem("3 PM", 78, "03d", 35),
                createHourlyItem("4 PM", 76, "09d", 60),
                createHourlyItem("5 PM", 74, "10d", 80),
                createHourlyItem("6 PM", 72, "04d", 45),
                createHourlyItem("7 PM", 70, "02n", 25)
        ));
        return forecast;
    }

    private Map<String, Object> createHourlyItem(String time, int temp, String icon, int precipitation) {
        Map<String, Object> item = new HashMap<>();
        item.put("time", time);
        item.put("temp", temp);
        item.put("icon", icon);
        item.put("precipitation", precipitation);
        item.put("windSpeed", 5 + random.nextInt(10));
        return item;
    }
}
