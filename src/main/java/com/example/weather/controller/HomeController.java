package com.example.weather.controller;

import com.example.weather.entity.Location;
import com.example.weather.entity.WeatherData;
import com.example.weather.service.LocationAPI;
import com.example.weather.service.WeatherService;
import com.example.weather.service.WeatherService.HourlyForecast;
import com.example.weather.service.WeatherService.DailyForecast;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class HomeController {

    @Autowired
    private LocationAPI locationAPI;

    @Autowired
    private WeatherService weatherService;

    // 🔁 Redirect root "/" to business homepage
    @GetMapping("/")
    public String redirectToBusiness() {
        return "redirect:/weather-app/";
    }

    // 🌀 Weather detail dashboard route
    @GetMapping("/weather-detailed")
    public String weatherDetailed(Model model,
                                  @RequestParam(required = false) String city,
                                  @RequestParam(required = false) String country,
                                  @RequestParam(required = false) Double lat,
                                  @RequestParam(required = false) Double lon) {
        return loadWeatherDashboard(model, city, country, lat, lon);
    }

    private String loadWeatherDashboard(Model model, String city, String country, Double lat, Double lon) {
        // (Same logic as your original implementation — no changes here)
        // It loads index.html with weather, location, hourly, daily info
        // ...
        return "index";
    }

    private Location createLocationFromWeather(WeatherData weather) {
        Location location = new Location();
        location.setCityName(weather.getCity());
        location.setCity(weather.getCity());
        location.setCountry(weather.getCountry());
        location.setLatitude(weather.getLatitude());
        location.setLongitude(weather.getLongitude());
        return location;
    }

    private Location createLocation(String city, String country) {
        Location location = new Location();
        location.setCityName(city);
        location.setCity(city);
        location.setCountry(country);
        return location;
    }

    private WeatherViewModel createWeatherViewModel(WeatherData weather) {
        WeatherViewModel viewModel = new WeatherViewModel();

        // Use REAL data from API
        viewModel.setWeatherIcon("https://openweathermap.org/img/wn/" +
                (weather.getWeatherIcon() != null ? weather.getWeatherIcon() : "01d") + "@2x.png");
        viewModel.setWeatherDescription(weather.getDescription() != null ?
                weather.getDescription() : "Clear sky");
        viewModel.setTempValue(weather.getTemperature() != null ?
                weather.getTemperature().intValue() : 25);
        viewModel.setFeelsLikeValue(weather.getFeelsLike() != null ?
                weather.getFeelsLike().intValue() : 27);
        viewModel.setWeatherMain(weather.getWeatherMain() != null ?
                weather.getWeatherMain() : "Clear");

        // Real weather details
        viewModel.setVisibility(weather.getVisibility() != null ?
                weather.getVisibility().intValue() : 10);
        viewModel.setHumidity(weather.getHumidity() != null ?
                weather.getHumidity() : 65);
        viewModel.setPressure(weather.getPressure() != null ?
                weather.getPressure().intValue() : 1013);
        viewModel.setWindSpeedValue(weather.getWindSpeed() != null ?
                weather.getWindSpeed().intValue() : 15);
        viewModel.setUvi(weather.getUvIndex() != null ?
                weather.getUvIndex().intValue() : 5);
        viewModel.setWindDegree(weather.getWindDirection() != null ?
                weather.getWindDirection() : 180);
        viewModel.setSunriseValue(weather.getSunrise() != null ?
                weather.getSunrise() : "06:30 AM");
        viewModel.setSunsetValue(weather.getSunset() != null ?
                weather.getSunset() : "07:45 PM");

        return viewModel;
    }

    private HourlyViewModel createHourlyViewModel(WeatherData weather) {
        HourlyViewModel hourly = new HourlyViewModel();

        if (weather.getHourlyForecast() != null && !weather.getHourlyForecast().isEmpty()) {
            HourlyForecast firstHour = weather.getHourlyForecast().get(0);
            hourly.setTimeValue(firstHour.getTime());
            hourly.setIconLink("https://openweathermap.org/img/wn/" + firstHour.getIcon() + "@2x.png");
            hourly.setTempValue(firstHour.getTemperature().intValue());
            hourly.setWindSpeedValue(firstHour.getWindSpeed().intValue());
            hourly.setRainPossibility(firstHour.getRainProbability());
        } else {
            // Use current weather data
            hourly.setTimeValue("Now");
            hourly.setIconLink("https://openweathermap.org/img/wn/" +
                    (weather.getWeatherIcon() != null ? weather.getWeatherIcon() : "01d") + "@2x.png");
            hourly.setTempValue(weather.getTemperature() != null ?
                    weather.getTemperature().intValue() : 25);
            hourly.setWindSpeedValue(weather.getWindSpeed() != null ?
                    weather.getWindSpeed().intValue() : 12);
            hourly.setRainPossibility(20);
        }

        return hourly;
    }

    private DailyViewModel createDailyViewModel(WeatherData weather) {
        DailyViewModel daily = new DailyViewModel();

        if (weather.getDailyForecast() != null && !weather.getDailyForecast().isEmpty()) {
            DailyForecast todayForecast = weather.getDailyForecast().get(0);
            daily.setMorningTemp(todayForecast.getMorningTemp().intValue());
            daily.setDayTemp(todayForecast.getDayTemp().intValue());
            daily.setNightTemp(todayForecast.getNightTemp().intValue());
        } else {
            // Use current temperature as base
            int currentTemp = weather.getTemperature() != null ?
                    weather.getTemperature().intValue() : 25;
            daily.setMorningTemp(currentTemp - 7);
            daily.setDayTemp(currentTemp);
            daily.setNightTemp(currentTemp - 10);
        }

        return daily;
    }

    // Default methods remain the same...
    private Location createDefaultLocation() {
        Location location = new Location();
        location.setCityName("New York");
        location.setCity("New York");
        location.setCountry("US");
        location.setCurrentDateTime(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy - HH:mm")));
        return location;
    }

    private WeatherViewModel createDefaultWeather() {
        WeatherViewModel viewModel = new WeatherViewModel();
        viewModel.setWeatherIcon("https://openweathermap.org/img/wn/01d@2x.png");
        viewModel.setWeatherDescription("API key required for real data");
        viewModel.setTempValue(25);
        viewModel.setFeelsLikeValue(27);
        viewModel.setWeatherMain("Clear");
        viewModel.setVisibility(10);
        viewModel.setHumidity(65);
        viewModel.setPressure(1013);
        viewModel.setWindSpeedValue(15);
        viewModel.setUvi(5);
        viewModel.setWindDegree(180);
        viewModel.setSunriseValue("06:30 AM");
        viewModel.setSunsetValue("07:45 PM");
        return viewModel;
    }

    private HourlyViewModel createDefaultHourlyViewModel() {
        HourlyViewModel hourly = new HourlyViewModel();
        hourly.setTimeValue("Now");
        hourly.setIconLink("https://openweathermap.org/img/wn/01d@2x.png");
        hourly.setTempValue(25);
        hourly.setWindSpeedValue(12);
        hourly.setRainPossibility(20);
        return hourly;
    }

    private DailyViewModel createDefaultDailyViewModel() {
        DailyViewModel daily = new DailyViewModel();
        daily.setMorningTemp(18);
        daily.setDayTemp(25);
        daily.setNightTemp(15);
        return daily;
    }

    // Inner classes for view models (same as before)
    public static class WeatherViewModel {
        private String weatherIcon;
        private String weatherDescription;
        private Integer tempValue;
        private Integer feelsLikeValue;
        private String weatherMain;
        private Integer visibility;
        private Integer humidity;
        private Integer pressure;
        private Integer windSpeedValue;
        private Integer uvi;
        private Integer windDegree;
        private String sunriseValue;
        private String sunsetValue;

        // Getters and Setters
        public String getWeatherIcon() { return weatherIcon; }
        public void setWeatherIcon(String weatherIcon) { this.weatherIcon = weatherIcon; }

        public String getWeatherDescription() { return weatherDescription; }
        public void setWeatherDescription(String weatherDescription) { this.weatherDescription = weatherDescription; }

        public Integer getTempValue() { return tempValue; }
        public void setTempValue(Integer tempValue) { this.tempValue = tempValue; }

        public Integer getFeelsLikeValue() { return feelsLikeValue; }
        public void setFeelsLikeValue(Integer feelsLikeValue) { this.feelsLikeValue = feelsLikeValue; }

        public String getWeatherMain() { return weatherMain; }
        public void setWeatherMain(String weatherMain) { this.weatherMain = weatherMain; }

        public Integer getVisibility() { return visibility; }
        public void setVisibility(Integer visibility) { this.visibility = visibility; }

        public Integer getHumidity() { return humidity; }
        public void setHumidity(Integer humidity) { this.humidity = humidity; }

        public Integer getPressure() { return pressure; }
        public void setPressure(Integer pressure) { this.pressure = pressure; }

        public Integer getWindSpeedValue() { return windSpeedValue; }
        public void setWindSpeedValue(Integer windSpeedValue) { this.windSpeedValue = windSpeedValue; }

        public Integer getUvi() { return uvi; }
        public void setUvi(Integer uvi) { this.uvi = uvi; }

        public Integer getWindDegree() { return windDegree; }
        public void setWindDegree(Integer windDegree) { this.windDegree = windDegree; }

        public String getSunriseValue() { return sunriseValue; }
        public void setSunriseValue(String sunriseValue) { this.sunriseValue = sunriseValue; }

        public String getSunsetValue() { return sunsetValue; }
        public void setSunsetValue(String sunsetValue) { this.sunsetValue = sunsetValue; }
    }

    public static class HourlyViewModel {
        private String timeValue;
        private String iconLink;
        private Integer tempValue;
        private Integer windSpeedValue;
        private Integer rainPossibility;

        // Getters and Setters
        public String getTimeValue() { return timeValue; }
        public void setTimeValue(String timeValue) { this.timeValue = timeValue; }

        public String getIconLink() { return iconLink; }
        public void setIconLink(String iconLink) { this.iconLink = iconLink; }

        public Integer getTempValue() { return tempValue; }
        public void setTempValue(Integer tempValue) { this.tempValue = tempValue; }

        public Integer getWindSpeedValue() { return windSpeedValue; }
        public void setWindSpeedValue(Integer windSpeedValue) { this.windSpeedValue = windSpeedValue; }

        public Integer getRainPossibility() { return rainPossibility; }
        public void setRainPossibility(Integer rainPossibility) { this.rainPossibility = rainPossibility; }
    }

    public static class DailyViewModel {
        private Integer morningTemp;
        private Integer dayTemp;
        private Integer nightTemp;

        // Getters and Setters
        public Integer getMorningTemp() { return morningTemp; }
        public void setMorningTemp(Integer morningTemp) { this.morningTemp = morningTemp; }

        public Integer getDayTemp() { return dayTemp; }
        public void setDayTemp(Integer dayTemp) { this.dayTemp = dayTemp; }

        public Integer getNightTemp() { return nightTemp; }
        public void setNightTemp(Integer nightTemp) { this.nightTemp = nightTemp; }
    }
}
