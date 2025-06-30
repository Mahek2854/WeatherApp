package com.example.weather.controller;

import com.example.weather.entity.Location;
import com.example.weather.entity.RecentLocation;
import com.example.weather.service.LocationSearchService;
import com.example.weather.service.RecentLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;

@Controller
@RequestMapping("/search") // NO CONTEXT PATH NEEDED NOW
public class SearchController {

    @Autowired(required = false)
    private LocationSearchService locationSearchService;

    @Autowired(required = false)
    private RecentLocationService recentLocationService;

    @GetMapping("/suggestions")
    @ResponseBody
    public ResponseEntity<List<Location>> getLocationSuggestions(@RequestParam String query) {
        try {
            if (locationSearchService != null) {
                List<Location> suggestions = locationSearchService.searchLocations(query);
                return ResponseEntity.ok(suggestions);
            } else {
                // Return mock suggestions if service is not available
                List<Location> mockSuggestions = createMockSuggestions(query);
                return ResponseEntity.ok(mockSuggestions);
            }
        } catch (Exception e) {
            System.err.println("Error getting location suggestions: " + e.getMessage());
            // Return empty list on error
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    private List<Location> createMockSuggestions(String query) {
        List<Location> suggestions = new ArrayList<>();

        // Create some mock suggestions based on query
        if (query.toLowerCase().contains("new")) {
            Location newYork = new Location();
            newYork.setCityName("New York");
            newYork.setCountry("US");
            suggestions.add(newYork);
        }

        if (query.toLowerCase().contains("lon")) {
            Location london = new Location();
            london.setCityName("London");
            london.setCountry("GB");
            suggestions.add(london);
        }

        if (query.toLowerCase().contains("tok")) {
            Location tokyo = new Location();
            tokyo.setCityName("Tokyo");
            tokyo.setCountry("JP");
            suggestions.add(tokyo);
        }

        if (query.toLowerCase().contains("par")) {
            Location paris = new Location();
            paris.setCityName("Paris");
            paris.setCountry("FR");
            suggestions.add(paris);
        }

        if (query.toLowerCase().contains("syd")) {
            Location sydney = new Location();
            sydney.setCityName("Sydney");
            sydney.setCountry("AU");
            suggestions.add(sydney);
        }

        return suggestions;
    }

    @PostMapping("/location")
    public String searchLocation(@RequestParam String locationQuery,
                                 HttpSession session,
                                 Model model) {
        try {
            if (locationSearchService != null) {
                Location selectedLocation = locationSearchService.getLocationDetails(locationQuery);

                // Save to recent locations
                if (recentLocationService != null) {
                    recentLocationService.addRecentLocation(selectedLocation, session.getId());
                }

                // Redirect to weather page with location parameters
                return "redirect:/weather-detailed?city=" + selectedLocation.getCityName() +
                        "&country=" + selectedLocation.getCountry();
            } else {
                // Parse the query manually
                String[] parts = locationQuery.split(",");
                String city = parts[0].trim();
                String country = parts.length > 1 ? parts[1].trim() : "";

                return "redirect:/weather-detailed?city=" + city +
                        (country.isEmpty() ? "" : "&country=" + country);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Location not found. Please try again.");
            return "redirect:/";
        }
    }

    @GetMapping("/recent")
    @ResponseBody
    public ResponseEntity<List<RecentLocation>> getRecentLocations(HttpSession session) {
        try {
            if (recentLocationService != null) {
                List<RecentLocation> recentLocations = recentLocationService.getRecentLocations(session.getId());
                return ResponseEntity.ok(recentLocations);
            } else {
                return ResponseEntity.ok(new ArrayList<>());
            }
        } catch (Exception e) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
