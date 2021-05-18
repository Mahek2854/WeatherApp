package com.example.weather.controller;

import com.example.weather.entity.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.*;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class WeatherController {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private LocationAPI locationAPI;
    @Autowired
    private WeatherAPI weatherAPI;
    @Autowired
    private CoordAPI coordAPI;

    @GetMapping("/")
    public String getIndex(Model theModel, @ModelAttribute CurrentWeather theWeather,
                           Location theLocation, Coord theCoord,
                           HourlyWeather theHourlyWeather,
                           DailyWeather theDailyWeather) throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        /* location */
        String locationUrl = locationAPI.getUrl() + locationAPI.getKey() + locationAPI.getFormat();

        URL theUrl = new URL(locationUrl);
        HttpURLConnection conn = (HttpURLConnection) theUrl.openConnection();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuffer buffer = new StringBuffer();
        while ((inputLine = reader.readLine()) != null) {
            buffer.append(inputLine);
        }
        reader.close();
        JSONObject jsonObject = new JSONObject(buffer.toString());
        theLocation.setCityName(jsonObject.getString("cityName"));
        theLocation.setCountry(jsonObject.getString("countryName"));

        /* coord */
        String coordUrl = coordAPI.getCoordUrl() + theLocation.getCityName() + coordAPI.getCoordKey();
        ResponseEntity<String> coordResp = restTemplate.exchange(coordUrl, HttpMethod.GET,
                null, String.class);
        theCoord = mapper.readValue(coordResp.getBody(), Coord.class);

        /* weather */
        String url = weatherAPI.getUrlLat() + theCoord.getLatitude().toString() + weatherAPI.getUrlLon() +
                    theCoord.getLongitude().toString() + weatherAPI.getUrlExtra() + weatherAPI.getAppKey();

        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET,
                null, String.class);

        theWeather = mapper.readValue(resp.getBody(), CurrentWeather.class);
        theHourlyWeather = mapper.readValue(resp.getBody(), HourlyWeather.class);
        JSONObject object = new JSONObject(theHourlyWeather);
        theDailyWeather = mapper.readValue(resp.getBody(), DailyWeather.class);

        /* conversion */
        Integer visibilityConv = theWeather.getVisibility() / 1000;
        theWeather.setVisibility(visibilityConv);
        Double windSpeedConv = theWeather.getWindSpeed() * 3.6;
        theWeather.setWindSpeed(windSpeedConv);
        Double windHourlySpeedConv = theHourlyWeather.getWindSpeed() * 3.6;
        theHourlyWeather.setWindSpeed(windHourlySpeedConv);
        Double rainPossibility = theHourlyWeather.getPop() * 100;
        theHourlyWeather.setPop(rainPossibility);

        /* date conv */
        Instant instantSunset = Instant.ofEpochSecond(theWeather.getSunset());
        Instant instantSunrise = Instant.ofEpochSecond(theWeather.getSunrise());
        Date sunsetDate = Date.from( instantSunset );
        Date sunriseDate = Date.from( instantSunrise );
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        String formattedSunriseDate = dateFormat.format(sunriseDate);
        String formattedSunsetDate = dateFormat.format(sunsetDate);
        theWeather.setSunriseValue(formattedSunriseDate);
        theWeather.setSunsetValue(formattedSunsetDate);

        /* hourly time conv */
        Instant instantTime = Instant.ofEpochSecond(theHourlyWeather.getTime());
        Date timeDate = Date.from(instantTime);
        SimpleDateFormat timeFormat = new SimpleDateFormat("h a");
        String formattedTime = timeFormat.format(timeDate);
        theHourlyWeather.setTimeValue(formattedTime);

        theModel.addAttribute("weather", theWeather);
        theModel.addAttribute("location", theLocation);
        theModel.addAttribute("hourly", theHourlyWeather);
        theModel.addAttribute("daily", theDailyWeather);
        return "index";
    }

}
