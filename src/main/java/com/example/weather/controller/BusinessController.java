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
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

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

    @GetMapping("/weather-detailed")
    public String weatherDetailed(Model model) {
        // Add sample data for Sehore
        Map<String, Object> location = new HashMap<>();
        location.put("cityName", "Sehore");
        location.put("country", "IN");
        location.put("currentDateTime", "Wednesday, July 2, 2025 at 10:08 PM");

        Map<String, Object> weather = new HashMap<>();
        weather.put("tempValue", 24);
        weather.put("feelsLikeValue", 22);
        weather.put("weatherDescription", "overcast clouds");
        weather.put("weatherMain", "Clear");
        weather.put("weatherIcon", "01n");
        weather.put("visibility", 10);
        weather.put("humidity", 94);
        weather.put("pressure", 1001);
        weather.put("windSpeedValue", 3);
        weather.put("uvi", 5);
        weather.put("windDegree", 180);
        weather.put("sunriseValue", "06:30 AM");
        weather.put("sunsetValue", "07:45 PM");

        Map<String, Object> daily = new HashMap<>();
        daily.put("morningTemp", 18);
        daily.put("dayTemp", 25);
        daily.put("nightTemp", 15);

        model.addAttribute("location", location);
        model.addAttribute("weather", weather);
        model.addAttribute("daily", daily);
        model.addAttribute("pageTitle", "Weather Details - WeatherPro Business");

        return "index";
    }

    @GetMapping("/api/weather/current")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentWeather(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        try {
            System.out.println("Current weather API called with city: " + city + ", country: " + country + ", lat: " + lat + ", lon: " + lon);

            Map<String, Object> response = new HashMap<>();

            // Generate current weather data based on location
            Map<String, Object> currentWeather = generateCurrentWeatherData(city, country, lat, lon);

            Map<String, Object> weatherData = new HashMap<>();
            weatherData.put("current", currentWeather);
            weatherData.put("location", city != null ? city : "Current Location");
            weatherData.put("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            response.put("success", true);
            response.put("data", weatherData);

            System.out.println("Current weather API response: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in current weather API: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    private Map<String, Object> generateCurrentWeatherData(String city, String country, Double lat, Double lon) {
        Map<String, Object> weather = new HashMap<>();

        // Determine climate type
        ClimateType climate = determineClimateType(city, country, lat, lon);

        // Generate realistic current weather based on location
        int currentHour = LocalDateTime.now().getHour();
        int baseTemp = getBaseTemperature(climate);
        int tempVariation = getTemperatureVariation(currentHour, climate);
        int temperature = baseTemp + tempVariation + random.nextInt(3) - 1;

        weather.put("temp", temperature);
        weather.put("feelsLike", temperature + random.nextInt(4) - 2);
        weather.put("description", getWeatherDescription(climate, currentHour));
        weather.put("main", getWeatherMain(climate));
        weather.put("icon", getWeatherIcon(currentHour, climate, 0));
        weather.put("visibility", getVisibility(climate, getPrecipitation(climate, currentHour)));
        weather.put("humidity", getHumidity(climate));
        weather.put("pressure", 1013 + random.nextInt(20) - 10);
        weather.put("windSpeed", getWindSpeed(climate, currentHour));
        weather.put("uvIndex", getUVIndex(currentHour));
        weather.put("windDirection", 180 + random.nextInt(180) - 90);

        return weather;
    }

    private String getWeatherDescription(ClimateType climate, int hour) {
        boolean isNight = hour < 6 || hour > 20;

        switch (climate) {
            case DESERT:
                return isNight ? "clear night sky" : "sunny and hot";
            case TROPICAL:
                if (hour >= 14 && hour <= 17) {
                    return "afternoon thunderstorms possible";
                }
                return isNight ? "warm and humid night" : "hot and humid";
            case OCEANIC:
                return isNight ? "cloudy night" : "partly cloudy";
            case CONTINENTAL:
                return isNight ? "clear night" : "partly sunny";
            case MEDITERRANEAN:
                return isNight ? "mild night" : "pleasant and sunny";
            default:
                return isNight ? "clear night" : "partly cloudy";
        }
    }

    private String getWeatherMain(ClimateType climate) {
        switch (climate) {
            case DESERT: return "Clear";
            case TROPICAL: return random.nextDouble() < 0.3 ? "Rain" : "Clouds";
            case OCEANIC: return "Clouds";
            case CONTINENTAL: return random.nextDouble() < 0.2 ? "Clouds" : "Clear";
            case MEDITERRANEAN: return "Clear";
            default: return "Clear";
        }
    }

    private int getUVIndex(int hour) {
        if (hour < 6 || hour > 18) return 0; // No UV at night
        if (hour < 10 || hour > 16) return random.nextInt(3) + 1; // Low UV
        return random.nextInt(6) + 5; // High UV during midday
    }

    @GetMapping("/api/weather/forecast")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getForecast(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        try {
            System.out.println("Forecast API called with city: " + city + ", country: " + country + ", lat: " + lat + ", lon: " + lon);

            Map<String, Object> response = new HashMap<>();

            // Generate location-specific hourly forecast
            List<Map<String, Object>> hourlyForecast = generateLocationSpecificForecast(city, country, lat, lon);
            System.out.println("Generated " + hourlyForecast.size() + " hourly forecast items");

            Map<String, Object> forecastData = new HashMap<>();
            forecastData.put("hourly", hourlyForecast);
            forecastData.put("location", city != null ? city : "Current Location");
            forecastData.put("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

            response.put("success", true);
            response.put("data", forecastData);

            System.out.println("Forecast API response: " + response);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in forecast API: " + e.getMessage());
            e.printStackTrace();

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

    private List<Map<String, Object>> generateLocationSpecificForecast(String city, String country, Double lat, Double lon) {
        List<Map<String, Object>> forecast = new ArrayList<>();

        // Determine climate type based on city
        ClimateType climate = determineClimateType(city, country, lat, lon);
        System.out.println("Determined climate type: " + climate + " for city: " + city);

        // Get current hour
        int currentHour = LocalDateTime.now().getHour();

        // Generate 24 hours of forecast data
        for (int i = 0; i < 24; i++) {
            int hour = (currentHour + i) % 24;
            Map<String, Object> hourData = generateHourlyData(hour, i, climate);
            forecast.add(hourData);
        }

        System.out.println("Generated forecast with " + forecast.size() + " hours of data");
        return forecast;
    }

    private ClimateType determineClimateType(String city, String country, Double lat, Double lon) {
        if (city == null) return ClimateType.TEMPERATE;

        String cityLower = city.toLowerCase();
        String countryLower = country != null ? country.toLowerCase() : "";

        // Indian cities - mostly tropical/temperate
        if (countryLower.contains("in") || cityLower.contains("sehore") ||
                cityLower.contains("mumbai") || cityLower.contains("delhi") ||
                cityLower.contains("bangalore") || cityLower.contains("chennai")) {
            return ClimateType.TROPICAL;
        }

        // Desert climates
        if (cityLower.contains("dubai") || cityLower.contains("phoenix") ||
                cityLower.contains("las vegas") || cityLower.contains("riyadh")) {
            return ClimateType.DESERT;
        }

        // Tropical climates
        if (cityLower.contains("miami") || cityLower.contains("singapore") ||
                cityLower.contains("bangkok") || countryLower.contains("thailand")) {
            return ClimateType.TROPICAL;
        }

        // Cold/Continental climates
        if (cityLower.contains("moscow") || cityLower.contains("chicago") ||
                cityLower.contains("toronto") || cityLower.contains("minneapolis")) {
            return ClimateType.CONTINENTAL;
        }

        // Oceanic climates
        if (cityLower.contains("london") || cityLower.contains("seattle") ||
                cityLower.contains("vancouver") || countryLower.contains("uk")) {
            return ClimateType.OCEANIC;
        }

        // Mediterranean climates
        if (cityLower.contains("sydney") || cityLower.contains("melbourne") ||
                cityLower.contains("los angeles") || cityLower.contains("barcelona")) {
            return ClimateType.MEDITERRANEAN;
        }

        return ClimateType.TEMPERATE;
    }

    private Map<String, Object> generateHourlyData(int hour, int hoursFromNow, ClimateType climate) {
        Map<String, Object> data = new HashMap<>();

        // Time formatting
        String timeStr;
        if (hoursFromNow == 0) {
            timeStr = "NOW";
        } else {
            timeStr = String.format("%02d:00", hour);
        }
        data.put("time", timeStr);

        // Temperature based on climate and time of day
        int baseTemp = getBaseTemperature(climate);
        int tempVariation = getTemperatureVariation(hour, climate);
        int temperature = baseTemp + tempVariation + random.nextInt(3) - 1; // ±1°C random variation

        data.put("temp", temperature);
        data.put("tempF", Math.round(temperature * 9.0/5.0 + 32)); // Fahrenheit conversion

        // Weather icon based on time and climate
        String icon = getWeatherIcon(hour, climate, hoursFromNow);
        data.put("icon", icon);

        // Precipitation based on climate
        int precipitation = getPrecipitation(climate, hour);
        data.put("precipitation", precipitation);

        // Wind speed
        int windSpeed = getWindSpeed(climate, hour);
        data.put("windSpeed", windSpeed);

        // Visibility
        int visibility = getVisibility(climate, precipitation);
        data.put("visibility", visibility);

        // Additional details
        data.put("humidity", getHumidity(climate));
        data.put("pressure", 1013 + random.nextInt(20) - 10);

        return data;
    }

    private int getBaseTemperature(ClimateType climate) {
        switch (climate) {
            case DESERT: return 35; // Hot desert
            case TROPICAL: return 28; // Warm tropical
            case CONTINENTAL: return 15; // Variable continental
            case OCEANIC: return 12; // Cool oceanic
            case MEDITERRANEAN: return 22; // Mild Mediterranean
            default: return 20; // Temperate
        }
    }

    private int getTemperatureVariation(int hour, ClimateType climate) {
        // Temperature curve throughout the day
        double hourRadians = (hour - 6) * Math.PI / 12; // Peak at 2 PM (hour 14)
        double tempCurve = Math.sin(hourRadians);

        int maxVariation;
        switch (climate) {
            case DESERT: maxVariation = 15; break; // Large day/night variation
            case CONTINENTAL: maxVariation = 12; break;
            case MEDITERRANEAN: maxVariation = 8; break;
            case TROPICAL: maxVariation = 5; break; // Small variation
            case OCEANIC: maxVariation = 6; break;
            default: maxVariation = 10; break;
        }

        return (int) (tempCurve * maxVariation);
    }

    private String getWeatherIcon(int hour, ClimateType climate, int hoursFromNow) {
        boolean isNight = hour < 6 || hour > 20;
        String dayNight = isNight ? "n" : "d";

        // Weather progression logic
        if (climate == ClimateType.OCEANIC && random.nextDouble() < 0.4) {
            return "09" + dayNight; // Rain
        } else if (climate == ClimateType.TROPICAL && hour >= 14 && hour <= 17 && random.nextDouble() < 0.6) {
            return "11" + dayNight; // Afternoon thunderstorms
        } else if (climate == ClimateType.DESERT) {
            return "01" + dayNight; // Clear skies
        } else if (hoursFromNow > 12 && random.nextDouble() < 0.3) {
            return "03" + dayNight; // Scattered clouds
        } else if (random.nextDouble() < 0.2) {
            return "02" + dayNight; // Few clouds
        } else {
            return "01" + dayNight; // Clear
        }
    }

    private int getPrecipitation(ClimateType climate, int hour) {
        int basePrecipitation;
        switch (climate) {
            case OCEANIC: basePrecipitation = 40; break;
            case TROPICAL:
                // Higher chance in afternoon
                basePrecipitation = (hour >= 14 && hour <= 17) ? 70 : 30;
                break;
            case CONTINENTAL: basePrecipitation = 25; break;
            case MEDITERRANEAN: basePrecipitation = 15; break;
            case DESERT: basePrecipitation = 5; break;
            default: basePrecipitation = 20; break;
        }

        return Math.max(0, basePrecipitation + random.nextInt(20) - 10);
    }

    private int getWindSpeed(ClimateType climate, int hour) {
        int baseWind;
        switch (climate) {
            case OCEANIC: baseWind = 15; break;
            case CONTINENTAL: baseWind = 12; break;
            case DESERT: baseWind = 8; break;
            case TROPICAL: baseWind = 10; break;
            case MEDITERRANEAN: baseWind = 12; break;
            default: baseWind = 10; break;
        }

        // Wind tends to be stronger during day
        if (hour >= 10 && hour <= 16) {
            baseWind += 3;
        }

        return Math.max(0, baseWind + random.nextInt(8) - 4);
    }

    private int getVisibility(ClimateType climate, int precipitation) {
        int baseVisibility = 10; // km

        if (precipitation > 50) {
            baseVisibility = 3; // Poor visibility in heavy rain
        } else if (precipitation > 20) {
            baseVisibility = 6; // Reduced visibility in light rain
        }

        if (climate == ClimateType.DESERT) {
            baseVisibility = Math.max(baseVisibility, 15); // Generally clear in desert
        }

        return Math.max(1, baseVisibility + random.nextInt(4) - 2);
    }

    private int getHumidity(ClimateType climate) {
        int baseHumidity;
        switch (climate) {
            case TROPICAL: baseHumidity = 80; break;
            case OCEANIC: baseHumidity = 75; break;
            case CONTINENTAL: baseHumidity = 60; break;
            case MEDITERRANEAN: baseHumidity = 55; break;
            case DESERT: baseHumidity = 25; break;
            default: baseHumidity = 65; break;
        }

        return Math.max(10, Math.min(95, baseHumidity + random.nextInt(20) - 10));
    }

    private enum ClimateType {
        DESERT, TROPICAL, CONTINENTAL, OCEANIC, MEDITERRANEAN, TEMPERATE
    }
}
