package com.example.weather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.ZoneId;

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

    @Autowired
    private RestTemplate restTemplate;

    // Use the API key from your properties file
    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.base-url}")
    private String baseApiUrl;

    @Value("${weather.use.real.api:true}")
    private boolean useRealApi;

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
    public String weatherDetailed(
            @RequestParam(required = false, defaultValue = "Sehore") String city,
            @RequestParam(required = false, defaultValue = "IN") String country,
            Model model) {

        System.out.println("Weather detailed called with city: " + city + ", country: " + country);
        System.out.println("Using real API: " + useRealApi);

        try {
            if (useRealApi) {
                // Fetch REAL weather data from OpenWeatherMap API
                Map<String, Object> realWeatherData = fetchRealWeatherData(city, country);

                if (realWeatherData != null) {
                    model.addAttribute("location", realWeatherData.get("location"));
                    model.addAttribute("weather", realWeatherData.get("weather"));
                    model.addAttribute("daily", realWeatherData.get("daily"));
                    model.addAttribute("pageTitle", "Weather Details - " + city + " - WeatherPro Business");
                    model.addAttribute("isRealData", true);
                } else {
                    // Fallback to mock data if API fails
                    System.out.println("API failed, using fallback data");
                    addFallbackData(model, city, country);
                    model.addAttribute("isRealData", false);
                }
            } else {
                // Use mock data when real API is disabled
                addFallbackData(model, city, country);
                model.addAttribute("isRealData", false);
            }

        } catch (Exception e) {
            System.err.println("Error fetching real weather data: " + e.getMessage());
            e.printStackTrace();
            addFallbackData(model, city, country);
            model.addAttribute("isRealData", false);
        }

        return "index";
    }

    private Map<String, Object> fetchRealWeatherData(String city, String country) {
        try {
            // Build API URL using properties
            String cityQuery = country.isEmpty() ? city : city + "," + country;
            String url = baseApiUrl + "/weather?q=" + cityQuery + "&appid=" + apiKey + "&units=metric";

            System.out.println("Calling OpenWeatherMap API: " + url);

            // Make API call
            Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);

            if (apiResponse == null) {
                throw new RuntimeException("No response from weather API");
            }

            System.out.println("✅ API Response received for: " + apiResponse.get("name"));

            // Parse the real API response
            return parseWeatherApiResponse(apiResponse);

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch real weather data: " + e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseWeatherApiResponse(Map<String, Object> apiResponse) {
        Map<String, Object> result = new HashMap<>();

        try {
            // Location data
            Map<String, Object> location = new HashMap<>();
            location.put("cityName", apiResponse.get("name"));

            Map<String, Object> sys = (Map<String, Object>) apiResponse.get("sys");
            location.put("country", sys.get("country"));
            location.put("currentDateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")));

            // Weather data
            Map<String, Object> weather = new HashMap<>();
            Map<String, Object> main = (Map<String, Object>) apiResponse.get("main");
            List<Map<String, Object>> weatherList = (List<Map<String, Object>>) apiResponse.get("weather");
            Map<String, Object> weatherInfo = weatherList.get(0);
            Map<String, Object> wind = (Map<String, Object>) apiResponse.get("wind");

            // Temperature (already in Celsius due to units=metric)
            weather.put("tempValue", Math.round(((Number) main.get("temp")).doubleValue()));
            weather.put("feelsLikeValue", Math.round(((Number) main.get("feels_like")).doubleValue()));

            // Weather description and icon
            weather.put("weatherDescription", weatherInfo.get("description"));
            weather.put("weatherMain", weatherInfo.get("main"));
            weather.put("weatherIcon", weatherInfo.get("icon"));

            // Other weather data
            weather.put("humidity", main.get("humidity"));
            weather.put("pressure", main.get("pressure"));
            weather.put("visibility", apiResponse.get("visibility") != null ?
                    Math.round(((Number) apiResponse.get("visibility")).doubleValue() / 1000.0) : 10);

            // Wind data
            if (wind != null) {
                double windSpeedMs = wind.get("speed") != null ? ((Number) wind.get("speed")).doubleValue() : 0;
                weather.put("windSpeedValue", Math.round(windSpeedMs * 3.6)); // Convert m/s to km/h
                weather.put("windDegree", wind.get("deg") != null ? wind.get("deg") : 0);
            } else {
                weather.put("windSpeedValue", 0);
                weather.put("windDegree", 0);
            }

            // UV Index (not available in current weather API, set to 0)
            weather.put("uvi", 0);

            // Sunrise/Sunset
            if (sys != null) {
                Long sunrise = sys.get("sunrise") != null ? ((Number) sys.get("sunrise")).longValue() : null;
                Long sunset = sys.get("sunset") != null ? ((Number) sys.get("sunset")).longValue() : null;

                if (sunrise != null) {
                    weather.put("sunriseValue", Instant.ofEpochSecond(sunrise)
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("hh:mm a")));
                } else {
                    weather.put("sunriseValue", "06:30 AM");
                }

                if (sunset != null) {
                    weather.put("sunsetValue", Instant.ofEpochSecond(sunset)
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("hh:mm a")));
                } else {
                    weather.put("sunsetValue", "07:45 PM");
                }
            }

            // Daily temperatures (using current temp as base)
            Map<String, Object> daily = new HashMap<>();
            int currentTemp = ((Number) weather.get("tempValue")).intValue();
            daily.put("morningTemp", currentTemp - 5);
            daily.put("dayTemp", currentTemp + 2);
            daily.put("nightTemp", currentTemp - 8);

            result.put("location", location);
            result.put("weather", weather);
            result.put("daily", daily);

            return result;

        } catch (Exception e) {
            System.err.println("Error parsing API response: " + e.getMessage());
            return null;
        }
    }

    private void addFallbackData(Model model, String city, String country) {
        Map<String, Object> location = new HashMap<>();
        location.put("cityName", city);
        location.put("country", country);
        location.put("currentDateTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a")));

        Map<String, Object> weather = new HashMap<>();
        weather.put("tempValue", 24);
        weather.put("feelsLikeValue", 22);
        weather.put("weatherDescription", "sample data - API unavailable");
        weather.put("weatherMain", "Clear");
        weather.put("weatherIcon", "01n");
        weather.put("visibility", 10);
        weather.put("humidity", 65);
        weather.put("pressure", 1013);
        weather.put("windSpeedValue", 5);
        weather.put("uvi", 3);
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
        model.addAttribute("pageTitle", "Weather Details - " + city + " - WeatherPro Business");
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

            if (useRealApi) {
                List<Map<String, Object>> hourlyForecast = fetchRealHourlyForecast(city, country, lat, lon);

                Map<String, Object> forecastData = new HashMap<>();
                forecastData.put("hourly", hourlyForecast);
                forecastData.put("location", city != null ? city : "Current Location");
                forecastData.put("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

                response.put("success", true);
                response.put("data", forecastData);
            } else {
                // Return mock forecast data
                response.put("success", false);
                response.put("error", "Real API is disabled");
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error in forecast API: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchRealHourlyForecast(String city, String country, Double lat, Double lon) {
        try {
            String url;
            if (lat != null && lon != null) {
                url = baseApiUrl + "/forecast?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey + "&units=metric";
            } else {
                String cityQuery = country != null && !country.isEmpty() ? city + "," + country : city;
                url = baseApiUrl + "/forecast?q=" + cityQuery + "&appid=" + apiKey + "&units=metric";
            }

            System.out.println("Calling forecast API: " + url);

            Map<String, Object> apiResponse = restTemplate.getForObject(url, Map.class);

            if (apiResponse == null) {
                throw new RuntimeException("No forecast response from API");
            }

            List<Map<String, Object>> forecastList = (List<Map<String, Object>>) apiResponse.get("list");
            List<Map<String, Object>> hourlyForecast = new ArrayList<>();

            // Process first 24 hours
            for (int i = 0; i < Math.min(24, forecastList.size()); i++) {
                Map<String, Object> item = forecastList.get(i);
                Map<String, Object> hourData = new HashMap<>();

                // Time
                Long dt = ((Number) item.get("dt")).longValue();
                String timeStr = i == 0 ? "NOW" :
                        Instant.ofEpochSecond(dt)
                                .atZone(ZoneId.systemDefault())
                                .format(DateTimeFormatter.ofPattern("HH:mm"));
                hourData.put("time", timeStr);

                // Temperature
                Map<String, Object> main = (Map<String, Object>) item.get("main");
                hourData.put("temp", Math.round(((Number) main.get("temp")).doubleValue()));

                // Weather icon
                List<Map<String, Object>> weather = (List<Map<String, Object>>) item.get("weather");
                hourData.put("icon", weather.get(0).get("icon"));

                // Precipitation probability
                Object pop = item.get("pop");
                hourData.put("precipitation", pop != null ? Math.round(((Number) pop).doubleValue() * 100) : 0);

                // Wind speed
                Map<String, Object> wind = (Map<String, Object>) item.get("wind");
                if (wind != null && wind.get("speed") != null) {
                    double windSpeedMs = ((Number) wind.get("speed")).doubleValue();
                    hourData.put("windSpeed", Math.round(windSpeedMs * 3.6)); // Convert to km/h
                } else {
                    hourData.put("windSpeed", 0);
                }

                // Visibility
                Object visibility = item.get("visibility");
                hourData.put("visibility", visibility != null ?
                        Math.round(((Number) visibility).doubleValue() / 1000.0) : 10);

                hourlyForecast.add(hourData);
            }

            System.out.println("✅ Fetched " + hourlyForecast.size() + " hours of real forecast data");
            return hourlyForecast;

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch real forecast data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Keep your existing commute recommendations method...
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
}