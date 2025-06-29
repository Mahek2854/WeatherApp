package com.example.weather.repository;

import com.example.weather.entity.RecentLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentLocationRepository extends JpaRepository<RecentLocation, Long> {

    List<RecentLocation> findBySessionId(String sessionId);

    List<RecentLocation> findBySessionIdOrderByLastAccessedDesc(String sessionId);

    Optional<RecentLocation> findBySessionIdAndCityNameAndCountry(String sessionId, String cityName, String country);

    RecentLocation findByLatitudeAndLongitude(Double latitude, Double longitude);

    void deleteBySessionId(String sessionId);
}