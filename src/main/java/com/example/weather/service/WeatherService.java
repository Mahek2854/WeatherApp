package com.example.weather.service;

import com.example.weather.entity.WeatherData;
import com.example.weather.entity.CurrentWeather;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class WeatherService {

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${weather.api.key}")
    private String weatherApiKey;

    @Value("${weather.api.units:metric}")
    private String units;

    @Value("${weather.api.onecall-url}")
    private String oneCallApiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    public WeatherData getCompleteWeatherData(String city, String country) {
        System.out.println("=== FETCHING COMPREHENSIVE WEATHER DATA ===");
        System.out.println("City: " + city + ", Country: " + country);

        try {
            // Get current weather data
            CurrentWeather currentWeather = getCurrentWeather(city, country);

            if (currentWeather != null) {
                // Get additional data using coordinates
                WeatherData enhancedData = getEnhancedWeatherData(
                        currentWeather.getLatitude(),
                        currentWeather.getLongitude()
                );

                // Merge current weather with enhanced data
                enhancedData.setCity(city);
                enhancedData.setCountry(country);
                enhancedData.setTemperature(currentWeather.getTemperature());
                enhancedData.setDescription(currentWeather.getDescription());
                enhancedData.setHumidity(currentWeather.getHumidity());
                enhancedData.setPressure(currentWeather.getPressure());
                enhancedData.setWindSpeed(currentWeather.getWindSpeed());
                enhancedData.setLatitude(currentWeather.getLatitude());
                enhancedData.setLongitude(currentWeather.getLongitude());

                System.out.println("✅ Successfully fetched comprehensive weather data!");
                return enhancedData;
            }

            return createDefaultWeatherData(city, country);
        } catch (Exception e) {
            System.err.println("❌ Error getting comprehensive weather data: " + e.getMessage());
            e.printStackTrace();
            return createDefaultWeatherData(city, country);
        }
    }

    public CurrentWeather getCurrentWeather(String city, String country) throws JsonProcessingException {
        try {
            if (weatherApiKey == null || weatherApiKey.equals("PUT_YOUR_ACTUAL_API_KEY_HERE") || weatherApiKey.trim().isEmpty()) {
                System.err.println("❌ API KEY NOT SET!");
                throw new RuntimeException("API key not configured");
            }

            String url = String.format("%s?q=%s,%s&appid=%s&units=%s",
                    weatherApiUrl, city, country, weatherApiKey, units);

            System.out.println("🌐 Making API call: " + url.replace(weatherApiKey, "***"));

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("cod") && !jsonNode.get("cod").asText().equals("200")) {
                String message = jsonNode.has("message") ? jsonNode.get("message").asText() : "Unknown API error";
                throw new RuntimeException("API Error: " + message);
            }

            CurrentWeather weather = new CurrentWeather();
            weather.setTemperature(jsonNode.get("main").get("temp").asDouble());
            weather.setDescription(jsonNode.get("weather").get(0).get("description").asText());
            weather.setHumidity(jsonNode.get("main").get("humidity").asInt());
            weather.setPressure(jsonNode.get("main").get("pressure").asDouble());

            // Wind data
            if (jsonNode.has("wind")) {
                if (jsonNode.get("wind").has("speed")) {
                    weather.setWindSpeed(jsonNode.get("wind").get("speed").asDouble());
                }
                if (jsonNode.get("wind").has("deg")) {
                    weather.setWindDirection(jsonNode.get("wind").get("deg").asInt());
                }
            }

            // Visibility
            if (jsonNode.has("visibility")) {
                weather.setVisibility(jsonNode.get("visibility").asDouble() / 1000.0); // Convert to km
            }

            // Sunrise and Sunset
            if (jsonNode.has("sys")) {
                JsonNode sys = jsonNode.get("sys");
                if (sys.has("sunrise")) {
                    weather.setSunrise(formatUnixTime(sys.get("sunrise").asLong()));
                }
                if (sys.has("sunset")) {
                    weather.setSunset(formatUnixTime(sys.get("sunset").asLong()));
                }
            }

            weather.setLatitude(jsonNode.get("coord").get("lat").asDouble());
            weather.setLongitude(jsonNode.get("coord").get("lon").asDouble());
            weather.setCity(city);
            weather.setCountry(country);

            return weather;

        } catch (Exception e) {
            System.err.println("❌ Error fetching current weather: " + e.getMessage());
            throw e;
        }
    }

    public WeatherData getEnhancedWeatherData(double latitude, double longitude) {
        try {
            // Use OneCall API for comprehensive data including UV Index
            String url = String.format("%s?lat=%f&lon=%f&appid=%s&units=%s&exclude=minutely,alerts",
                    oneCallApiUrl, latitude, longitude, weatherApiKey, units);

            System.out.println("🌐 Making OneCall API request: " + url.replace(weatherApiKey, "***"));

            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            WeatherData weatherData = new WeatherData();

            // Current weather data
            JsonNode current = jsonNode.get("current");
            weatherData.setTemperature(current.get("temp").asDouble());
            weatherData.setFeelsLike(current.get("feels_like").asDouble());
            weatherData.setHumidity(current.get("humidity").asInt());
            weatherData.setPressure(current.get("pressure").asDouble());
            weatherData.setUvIndex(current.has("uvi") ? current.get("uvi").asDouble() : 0.0);
            weatherData.setVisibility(current.has("visibility") ? current.get("visibility").asDouble() / 1000.0 : 10.0);

            // Wind data
            if (current.has("wind_speed")) {
                weatherData.setWindSpeed(current.get("wind_speed").asDouble() * 3.6); // Convert m/s to km/h
            }
            if (current.has("wind_deg")) {
                weatherData.setWindDirection(current.get("wind_deg").asInt());
            }

            // Sun times
            weatherData.setSunrise(formatUnixTime(current.get("sunrise").asLong()));
            weatherData.setSunset(formatUnixTime(current.get("sunset").asLong()));

            // Weather description
            weatherData.setDescription(current.get("weather").get(0).get("description").asText());
            weatherData.setWeatherMain(current.get("weather").get(0).get("main").asText());
            weatherData.setWeatherIcon(current.get("weather").get(0).get("icon").asText());

            // Hourly forecast (next 24 hours)
            if (jsonNode.has("hourly")) {
                weatherData.setHourlyForecast(parseHourlyForecast(jsonNode.get("hourly")));
            }

            // Daily forecast
            if (jsonNode.has("daily")) {
                weatherData.setDailyForecast(parseDailyForecast(jsonNode.get("daily")));
            }

            weatherData.setLatitude(latitude);
            weatherData.setLongitude(longitude);

            System.out.println("✅ Enhanced weather data fetched successfully!");
            System.out.println("UV Index: " + weatherData.getUvIndex());
            System.out.println("Visibility: " + weatherData.getVisibility() + " km");
            System.out.println("Wind Direction: " + weatherData.getWindDirection() + "°");

            return weatherData;

        } catch (Exception e) {
            System.err.println("❌ Error fetching enhanced weather data: " + e.getMessage());
            e.printStackTrace();
            return createDefaultWeatherData("Unknown", "Unknown");
        }
    }

    private List<HourlyForecast> parseHourlyForecast(JsonNode hourlyNode) {
        List<HourlyForecast> hourlyList = new ArrayList<>();

        for (int i = 0; i < Math.min(24, hourlyNode.size()); i++) {
            JsonNode hour = hourlyNode.get(i);
            HourlyForecast forecast = new HourlyForecast();

            forecast.setTime(formatUnixTimeHour(hour.get("dt").asLong()));
            forecast.setTemperature(hour.get("temp").asDouble());
            forecast.setWindSpeed(hour.get("wind_speed").asDouble() * 3.6); // Convert to km/h
            forecast.setRainProbability(hour.has("pop") ? (int)(hour.get("pop").asDouble() * 100) : 0);
            forecast.setIcon(hour.get("weather").get(0).get("icon").asText());

            hourlyList.add(forecast);
        }

        return hourlyList;
    }

    private List<DailyForecast> parseDailyForecast(JsonNode dailyNode) {
        List<DailyForecast> dailyList = new ArrayList<>();

        if (dailyNode.size() > 0) {
            JsonNode today = dailyNode.get(0);
            DailyForecast forecast = new DailyForecast();

            JsonNode temp = today.get("temp");
            forecast.setMorningTemp(temp.get("morn").asDouble());
            forecast.setDayTemp(temp.get("day").asDouble());
            forecast.setNightTemp(temp.get("night").asDouble());

            dailyList.add(forecast);
        }

        return dailyList;
    }

    private String formatUnixTime(long unixTime) {
        return Instant.ofEpochSecond(unixTime)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("hh:mm a"));
    }

    private String formatUnixTimeHour(long unixTime) {
        LocalDateTime dateTime = Instant.ofEpochSecond(unixTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        if (dateTime.getHour() == LocalDateTime.now().getHour()) {
            return "Now";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public WeatherData getWeatherByCoordinates(double latitude, double longitude) {
        try {
            return getEnhancedWeatherData(latitude, longitude);
        } catch (Exception e) {
            System.err.println("❌ Error fetching weather by coordinates: " + e.getMessage());
            return createDefaultWeatherData("Unknown", "Unknown");
        }
    }

    private WeatherData createDefaultWeatherData(String city, String country) {
        System.out.println("⚠️ Using default weather data - API call failed");
        WeatherData defaultData = new WeatherData();
        defaultData.setCity(city);
        defaultData.setCountry(country);
        defaultData.setTemperature(20.0);
        defaultData.setFeelsLike(22.0);
        defaultData.setDescription("Weather data unavailable - Check API key");
        defaultData.setHumidity(50);
        defaultData.setPressure(1013.25);
        defaultData.setWindSpeed(5.0);
        defaultData.setWindDirection(180);
        defaultData.setVisibility(10.0);
        defaultData.setUvIndex(5.0);
        defaultData.setSunrise("06:30 AM");
        defaultData.setSunset("07:45 PM");
        return defaultData;
    }

    // Inner classes for forecast data
    public static class HourlyForecast {
        private String time;
        private Double temperature;
        private Double windSpeed;
        private Integer rainProbability;
        private String icon;

        // Getters and Setters
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }

        public Double getTemperature() { return temperature; }
        public void setTemperature(Double temperature) { this.temperature = temperature; }

        public Double getWindSpeed() { return windSpeed; }
        public void setWindSpeed(Double windSpeed) { this.windSpeed = windSpeed; }

        public Integer getRainProbability() { return rainProbability; }
        public void setRainProbability(Integer rainProbability) { this.rainProbability = rainProbability; }

        public String getIcon() { return icon; }
        public void setIcon(String icon) { this.icon = icon; }
    }

    public static class DailyForecast {
        private Double morningTemp;
        private Double dayTemp;
        private Double nightTemp;

        // Getters and Setters
        public Double getMorningTemp() { return morningTemp; }
        public void setMorningTemp(Double morningTemp) { this.morningTemp = morningTemp; }

        public Double getDayTemp() { return dayTemp; }
        public void setDayTemp(Double dayTemp) { this.dayTemp = dayTemp; }

        public Double getNightTemp() { return nightTemp; }
        public void setNightTemp(Double nightTemp) { this.nightTemp = nightTemp; }
    }
}