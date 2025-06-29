package com.example.weather.service;

import com.example.weather.entity.Location;
import com.example.weather.entity.RecentLocation;
import com.example.weather.repository.RecentLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RecentLocationService {

    @Autowired
    private RecentLocationRepository recentLocationRepository;

    public List<RecentLocation> getRecentLocations(String sessionId) {
        return recentLocationRepository.findBySessionIdOrderByLastAccessedDesc(sessionId);
    }

    public void addRecentLocation(Location location, String sessionId) {
        // Check if location already exists for this session
        Optional<RecentLocation> existing = recentLocationRepository
                .findBySessionIdAndCityNameAndCountry(sessionId, location.getCityName(), location.getCountry());

        if (existing.isPresent()) {
            // Update last accessed time
            RecentLocation recentLocation = existing.get();
            recentLocation.setLastAccessed(LocalDateTime.now());
            recentLocationRepository.save(recentLocation);
        } else {
            // Create new recent location entry
            RecentLocation newRecentLocation = new RecentLocation(
                    sessionId,
                    location.getCityName(),
                    location.getCountry(),
                    location.getLatitude(),
                    location.getLongitude()
            );

            // Set state if available
            if (location.getState() != null) {
                newRecentLocation.setState(location.getState());
            }

            recentLocationRepository.save(newRecentLocation);
        }

        // Keep only the last 10 recent locations per session
        cleanupOldLocations(sessionId);
    }

    public void saveLocation(Location location) {
        // This method might be used elsewhere, keeping for compatibility
        if (location.getLatitude() != null && location.getLongitude() != null) {
            // You might want to save to a different repository or handle differently
            System.out.println("Saving location: " + location);
        }
    }

    public RecentLocation findByCoordinates(double latitude, double longitude) {
        return recentLocationRepository.findByLatitudeAndLongitude(latitude, longitude);
    }

    private void cleanupOldLocations(String sessionId) {
        List<RecentLocation> allLocations = recentLocationRepository
                .findBySessionIdOrderByLastAccessedDesc(sessionId);

        if (allLocations.size() > 10) {
            List<RecentLocation> locationsToDelete = allLocations.subList(10, allLocations.size());
            recentLocationRepository.deleteAll(locationsToDelete);
        }
    }

    public void clearRecentLocations(String sessionId) {
        List<RecentLocation> locations = recentLocationRepository.findBySessionId(sessionId);
        recentLocationRepository.deleteAll(locations);
    }
}