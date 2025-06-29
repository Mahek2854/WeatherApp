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

@Controller
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private LocationSearchService locationSearchService;

    @Autowired
    private RecentLocationService recentLocationService;

    @GetMapping("/suggestions")
    @ResponseBody
    public ResponseEntity<List<Location>> getLocationSuggestions(@RequestParam String query) {
        List<Location> suggestions = locationSearchService.searchLocations(query);
        return ResponseEntity.ok(suggestions);
    }

    @PostMapping("/location")
    public String searchLocation(@RequestParam String locationQuery,
                                 HttpSession session,
                                 Model model) {
        try {
            Location selectedLocation = locationSearchService.getLocationDetails(locationQuery);

            // Save to recent locations
            recentLocationService.addRecentLocation(selectedLocation, session.getId());

            // Redirect to weather page with location parameters
            return "redirect:/weather?city=" + selectedLocation.getCityName() +
                    "&country=" + selectedLocation.getCountry();
        } catch (Exception e) {
            model.addAttribute("error", "Location not found. Please try again.");
            return "redirect:/";
        }
    }

    @GetMapping("/recent")
    @ResponseBody
    public ResponseEntity<List<RecentLocation>> getRecentLocations(HttpSession session) {
        List<RecentLocation> recentLocations = recentLocationService.getRecentLocations(session.getId());
        return ResponseEntity.ok(recentLocations);
    }
}
