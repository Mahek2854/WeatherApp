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

    // This RestTemplate is for OpenWeatherMap (autowired by Spring)
    @Autowired
    private RestTemplate restTemplate;

    // Use the API key from your properties file for OpenWeatherMap
    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.base-url}")
    private String baseApiUrl;

    @Value("${weather.use.real.api:true}")
    private boolean useRealApi;

    // WeatherAPI.com properties
    @Value("${weatherapi.base-url}")
    private String weatherApiBaseUrl;

    @Value("${weatherapi.key}")
    private String weatherApiKey;

    // This RestTemplate is specifically for WeatherAPI.com (as you clarified)
    private final RestTemplate weatherApiRestTemplate = new RestTemplate();

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
                // Fetch REAL weather data from OpenWeatherMap API using the autowired restTemplate
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
            // Build API URL using properties for OpenWeatherMap
            String cityQuery = country.isEmpty() ? city : city + "," + country;
            String url = baseApiUrl + "/weather?q=" + cityQuery + "&appid=" + apiKey + "&units=metric";

            System.out.println("Calling OpenWeatherMap API: " + url);

            // Make API call using the autowired restTemplate
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

    // This is your existing forecast endpoint. We will modify it to use WeatherAPI.com
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
                // Call the updated fetchRealHourlyForecast method which now uses WeatherAPI.com
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
            String locationQuery;

            if (lat != null && lon != null) {
                locationQuery = lat + "," + lon;
            } else {
                locationQuery = country != null && !country.isEmpty() ? city + "," + country : city;
            }

            // Use WeatherAPI.com for hourly forecast
            url = weatherApiBaseUrl + "/forecast.json?key=" + weatherApiKey + "&q=" + locationQuery + "&days=1";

            System.out.println("Calling WeatherAPI.com forecast API: " + url);

            // Make API call using the dedicated weatherApiRestTemplate
            Map<String, Object> apiResponse = weatherApiRestTemplate.getForObject(url, Map.class);

            if (apiResponse == null) {
                throw new RuntimeException("No forecast response from WeatherAPI.com");
            }

            List<Map<String, Object>> hourlyForecast = new ArrayList<>();

            Map<String, Object> forecast = (Map<String, Object>) apiResponse.get("forecast");
            if (forecast != null) {
                List<Map<String, Object>> forecastday = (List<Map<String, Object>>) forecast.get("forecastday");
                if (forecastday != null && !forecastday.isEmpty()) {
                    List<Map<String, Object>> hourList = (List<Map<String, Object>>) forecastday.get(0).get("hour");

                    if (hourList != null) {
                        // Process all available hours for the current day
                        for (Map<String, Object> item : hourList) {
                            Map<String, Object> hourData = new HashMap<>();

                            // Time (WeatherAPI.com provides "YYYY-MM-DD HH:mm" format)
                            String timeFull = (String) item.get("time");
                            String timeStr = LocalDateTime.parse(timeFull, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                                    .format(DateTimeFormatter.ofPattern("HH:mm"));
                            hourData.put("time", timeStr);

                            // Temperature
                            hourData.put("temp", Math.round(((Number) item.get("temp_c")).doubleValue()));

                            // Weather icon
                            Map<String, Object> condition = (Map<String, Object>) item.get("condition");
                            if (condition != null) {
                                // WeatherAPI.com provides full URL for icon, use it directly
                                hourData.put("icon", condition.get("icon"));
                            } else {
                                hourData.put("icon", "//cdn.weatherapi.com/weather/64x64/day/113.png"); // Default clear icon
                            }

                            // Precipitation probability (chance_of_rain is percentage)
                            hourData.put("precipitation", ((Number) item.get("chance_of_rain")).intValue());

                            // Wind speed (wind_kph is already in km/h)
                            hourData.put("windSpeed", Math.round(((Number) item.get("wind_kph")).doubleValue()));

                            // Visibility (vis_km is already in km)
                            hourData.put("visibility", Math.round(((Number) item.get("vis_km")).doubleValue()));

                            hourlyForecast.add(hourData);
                        }
                    }
                }
            }

            System.out.println("✅ Fetched " + hourlyForecast.size() + " hours of real forecast data from WeatherAPI.com");
            return hourlyForecast;

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch real forecast data from WeatherAPI.com: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
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

    @GetMapping("/safety")
    public String safety(Model model) {
        model.addAttribute("pageTitle", "Weather Safety Center - WeatherPro Business");
        return "safety";
    }

    @GetMapping("/alerts")
    public String alerts(Model model) {
        model.addAttribute("pageTitle", "Weather Alerts & Warnings - WeatherPro Business");
        return "alerts";
    }

    // NEW: API endpoint for current weather data for the dashboard
    @GetMapping("/api/weather/current-data")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCurrentWeatherDataForDashboard(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {
        try {
            System.out.println("🌤️ Dashboard current weather API called for: " + (city != null ? city : "coords " + lat + "," + lon));

            WeatherData weather = null;
            if (lat != null && lon != null) {
                weather = weatherService.getWeatherByCoordinates(lat, lon);
            } else if (city != null && !city.trim().isEmpty()) {
                weather = weatherService.getCompleteWeatherData(city, country != null ? country : "");
            }

            Map<String, Object> response = new HashMap<>();
            if (weather != null) {
                response.put("success", true);
                response.put("data", weather);
                System.out.println("✅ Dashboard current weather data retrieved successfully");
            } else {
                response.put("success", false);
                response.put("error", "No weather data found for the specified location.");
                System.err.println("❌ No dashboard current weather data found");
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error in dashboard current weather API: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to fetch current weather data: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/api/alerts/real-time")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getRealTimeAlerts(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon) {

        try {
            System.out.println("🚨 Real-time alerts API called with lat: " + lat + ", lon: " + lon);

            Map<String, Object> response = new HashMap<>();
            List<Map<String, Object>> alerts = new ArrayList<>();

            if (useRealApi && lat != null && lon != null) {
                // Use WeatherAPI.com for real alerts
                alerts = fetchWeatherApiAlerts(lat, lon);
            } else {
                // Fallback to mock data for testing or if API is disabled/coordinates missing
                alerts = generateMockAlerts(lat, lon);
            }

            response.put("success", true);
            response.put("alerts", alerts);
            response.put("lastUpdated", LocalDateTime.now().toString());

            System.out.println("✅ Returning " + alerts.size() + " alerts");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("❌ Error in alerts API: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.ok(errorResponse);
        }
    }

    // Add this helper method to generate mock alerts data
    private List<Map<String, Object>> generateMockAlerts(Double lat, Double lon) {
        List<Map<String, Object>> alerts = new ArrayList<>();

        // Sample alert 1
        Map<String, Object> alert1 = new HashMap<>();
        alert1.put("title", "Severe Thunderstorm Warning");
        alert1.put("description", "A severe thunderstorm capable of producing damaging winds in excess of 60 mph and quarter size hail is approaching the area. Take shelter immediately.");
        alert1.put("status", "WARNING");
        alert1.put("severity", "severe");
        alert1.put("color", "#e74c3c");
        alert1.put("icon", "fas fa-bolt");
        alert1.put("areas", "Downtown Area, Business District");
        alert1.put("effective", LocalDateTime.now().minusMinutes(15).toString());
        alert1.put("expires", LocalDateTime.now().plusHours(2).toString());
        alert1.put("senderName", "National Weather Service");

        // Sample alert 2
        Map<String, Object> alert2 = new HashMap<>();
        alert2.put("title", "High Wind Watch");
        alert2.put("description", "Sustained winds of 35 to 45 mph with gusts up to 65 mph are possible. Secure loose outdoor items.");
        alert2.put("status", "WATCH");
        alert2.put("severity", "moderate");
        alert2.put("color", "#f39c12");
        alert2.put("icon", "fas fa-wind");
        alert2.put("areas", "Metropolitan Area");
        alert2.put("effective", LocalDateTime.now().plusHours(1).toString());
        alert2.put("expires", LocalDateTime.now().plusHours(8).toString());
        alert2.put("senderName", "National Weather Service");

        alerts.add(alert1);
        alerts.add(alert2);

        return alerts;
    }

    // WeatherAPI.com integration method
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> fetchWeatherApiAlerts(Double lat, Double lon) {
        List<Map<String, Object>> alerts = new ArrayList<>();

        try {
            String url = weatherApiBaseUrl + "/alerts.json?key=" + weatherApiKey +
                    "&q=" + lat + "," + lon + "&aqi=no";

            System.out.println("🌐 Calling WeatherAPI Alerts: " + url);

            // Use the dedicated weatherApiRestTemplate for alerts
            Map<String, Object> apiResponse = weatherApiRestTemplate.getForObject(url, Map.class);

            if (apiResponse != null) {
                Map<String, Object> alertsData = (Map<String, Object>) apiResponse.get("alerts");
                if (alertsData != null) {
                    List<Map<String, Object>> alertList = (List<Map<String, Object>>) alertsData.get("alert");

                    if (alertList != null) {
                        for (Map<String, Object> alertItem : alertList) {
                            Map<String, Object> alert = new HashMap<>();

                            alert.put("title", alertItem.get("headline"));
                            alert.put("description", alertItem.get("desc"));
                            alert.put("status", "ALERT"); // WeatherAPI doesn't always provide a status like NWS
                            alert.put("severity", alertItem.get("severity"));
                            alert.put("effective", alertItem.get("effective"));
                            alert.put("expires", alertItem.get("expires"));
                            alert.put("senderName", alertItem.get("msgtype")); // Using msgtype as senderName
                            alert.put("areas", alertItem.get("areas"));

                            // Set default color and icon, you can refine this based on severity/event
                            alert.put("color", "#f39c12"); // Default to orange for alerts
                            alert.put("icon", "fas fa-exclamation-triangle");

                            alerts.add(alert);
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to fetch WeatherAPI alerts: " + e.getMessage());
            // Return empty list, the calling method will handle fallback to mock data
        }

        return alerts;
    }
}
