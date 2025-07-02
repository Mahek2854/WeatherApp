package com.example.weather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.weather.service.WeatherService;
import com.example.weather.service.CommuteRecommendationService;
import com.example.weather.entity.WeatherData;

@Controller
@RequestMapping("/business") // CHANGED FROM /weather-app to /business
public class BusinessController {

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private CommuteRecommendationService commuteRecommendationService;

    @GetMapping("/")
    public String businessHome(Model model) {
        model.addAttribute("pageTitle", "WeatherPro Business - Professional Weather Services");
        return "home"; // Uses home.html (wrapped with layout.html)
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Weather Dashboard - WeatherPro Business");
        return "dashboard"; // Uses dashboard.html (wrapped with layout.html)
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
                // Get weather by coordinates
                var weather = weatherService.getWeatherByCoordinates(lat, lon);
                if (weather != null) {
                    response.put("success", true);
                    response.put("data", weather);
                    return ResponseEntity.ok(response);
                }
            } else if (city != null && !city.trim().isEmpty()) {
                // Get weather by city
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

            // For now, return mock forecast data
            // In a real implementation, you would call a forecast API
            response.put("success", true);
            response.put("data", generateMockForecast());

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

            // Get weather data first
            WeatherData weather = null;
            if (lat != null && lon != null) {
                weather = weatherService.getWeatherByCoordinates(lat, lon);
            } else if (city != null && !city.trim().isEmpty()) {
                weather = weatherService.getCompleteWeatherData(city, country != null ? country : "");
            }

            // Generate recommendations
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

    private Object generateMockForecast() {
        // Generate mock hourly forecast data
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
        return item;
    }
}
