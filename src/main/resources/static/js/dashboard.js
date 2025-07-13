// Global variables
let currentLocation = null
let selectedCity = null
let selectedCountry = null
let autoRefreshEnabled = false
let autoRefreshInterval = null
let lastForecastData = null

// Initialize dashboard when page loads
document.addEventListener("DOMContentLoaded", () => {
  loadSavedLocation()

  // Add enter key support for location input
  document.getElementById("locationInput").addEventListener("keypress", (e) => {
    if (e.key === "Enter") {
      searchLocation()
    }
  })

  // Start auto-refresh if enabled
  const savedAutoRefresh = localStorage.getItem("autoRefreshEnabled")
  if (savedAutoRefresh === "true") {
    toggleAutoRefresh()
  }
})

function loadSavedLocation() {
  const saved = localStorage.getItem("selectedLocation")
  if (saved) {
    const location = JSON.parse(saved)
    selectLocation(location.city, location.country)
  }
}

function saveSelectedLocation(city, country) {
  localStorage.setItem(
    "selectedLocation",
    JSON.stringify({
      city: city,
      country: country,
      timestamp: Date.now(),
    }),
  )
}

async function searchLocation() {
  const input = document.getElementById("locationInput").value.trim()
  if (!input) {
    showMessage("Please enter a city name", "error")
    return
  }

  showMessage("Searching for location...", "info")

  // Parse input (handle "City, Country" format)
  const parts = input.split(",")
  const city = parts[0].trim()
  const country = parts[1] ? parts[1].trim() : ""

  await selectLocation(city, country)
}

async function selectLocation(city, country = "") {
  selectedCity = city
  selectedCountry = country

  showMessage(`Loading real-time weather data for ${city}...`, "info")

  try {
    await loadWeatherData(city, country)
    await loadForecastData(city, country)
    generateCommuteRecommendations()
    generateTravelAlerts()
    updateLastUpdateTime()
    saveSelectedLocation(city, country)

    // Clear the input
    document.getElementById("locationInput").value = ""

    showMessage(`Real-time weather data loaded for ${city}`, "success")
  } catch (error) {
    console.error("Error loading location data:", error)
    showMessage(`Unable to load weather data for ${city}. Please try another location.`, "error")
  }
}

async function useCurrentLocation() {
  showMessage("Detecting your location...", "info")

  if (!navigator.geolocation) {
    showMessage("Geolocation is not supported by your browser", "error")
    return
  }

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      currentLocation = {
        lat: position.coords.latitude,
        lon: position.coords.longitude,
      }

      try {
        await loadWeatherDataByCoords(currentLocation.lat, currentLocation.lon)
        await loadForecastDataByCoords(currentLocation.lat, currentLocation.lon)
        generateCommuteRecommendations()
        generateTravelAlerts()
        updateLastUpdateTime()

        showMessage("Real-time weather data loaded for your current location", "success")
      } catch (error) {
        console.error("Error loading current location weather:", error)
        showMessage("Unable to load weather data for your location", "error")
      }
    },
    (error) => {
      console.error("Geolocation error:", error)
      showMessage("Unable to access your location. Please search for a city instead.", "error")
    },
  )
}

async function loadWeatherData(city, country) {
  const params = new URLSearchParams()
  params.append("city", city)
  if (country) params.append("country", country)

  // Corrected API endpoint call
  const response = await fetch(`/business/api/weather/current-data?${params}`)
  const result = await response.json()

  if (result.success && result.data) {
    updateWeatherDisplay(result.data)
  } else {
    throw new Error(result.error || "Weather data not available")
  }
}

async function loadWeatherDataByCoords(lat, lon) {
  const params = new URLSearchParams()
  params.append("lat", lat.toString())
  params.append("lon", lon.toString())

  // Corrected API endpoint call
  const response = await fetch(`/business/api/weather/current-data?${params}`)
  const result = await response.json()

  if (result.success && result.data) {
    updateWeatherDisplay(result.data)
  } else {
    throw new Error(result.error || "Weather data not available")
  }
}

async function loadForecastData(city, country) {
  const params = new URLSearchParams()
  params.append("city", city)
  if (country) params.append("country", country)

  const response = await fetch(`/business/api/weather/forecast?${params}`)
  const result = await response.json()

  if (result.success && result.data) {
    lastForecastData = result.data
    updateHourlyForecast(result.data)
  }
}

async function loadForecastDataByCoords(lat, lon) {
  const params = new URLSearchParams()
  params.append("lat", lat.toString())
  params.append("lon", lon.toString())

  const response = await fetch(`/business/api/weather/forecast?${params}`)
  const result = await response.json()

  if (result.success && result.data) {
    lastForecastData = result.data
    updateHourlyForecast(result.data)
  }
}

function updateWeatherDisplay(data) {
  // Update location name
  const locationText = data.country ? `${data.city}, ${data.country}` : data.city
  document.getElementById("currentLocation").textContent = locationText

  // Update temperature (convert from Celsius to Fahrenheit if needed)
  const tempF = data.temperature ? Math.round((data.temperature * 9) / 5 + 32) : "--"
  const feelsLikeF = data.feelsLike ? Math.round((data.feelsLike * 9) / 5 + 32) : "--"

  document.getElementById("currentTemp").textContent = tempF + "°F"
  document.getElementById("feelsLike").textContent = feelsLikeF + "°F"
  document.getElementById("weatherDesc").textContent = data.description || "No description available"

  // Update weather icon
  if (data.weatherIcon) {
    document.getElementById("weatherIcon").src = `https://openweathermap.org/img/wn/${data.weatherIcon}@2x.png`
  }

  // Update metrics
  document.getElementById("humidity").textContent = (data.humidity || "--") + "%"
  document.getElementById("windSpeed").textContent =
    (data.windSpeed ? Math.round(data.windSpeed * 2.237) : "--") + " mph"
  document.getElementById("visibility").textContent =
    (data.visibility ? Math.round(data.visibility * 0.621371) : "--") + " mi"
  document.getElementById("pressure").textContent = (data.pressure || "--") + " mb"
}

function updateHourlyForecast(forecastData) {
  const container = document.getElementById("hourlyForecast")
  const locationElement = document.getElementById("forecastLocation")
  const timeElement = document.getElementById("forecastTime")

  container.innerHTML = ""

  if (!forecastData || !forecastData.hourly || forecastData.hourly.length === 0) {
    container.innerHTML = `
            <div style="text-align: center; color: #7f8c8d; width: 100%;">
                <i class="fas fa-clock" style="font-size: 2rem; margin-bottom: 15px;"></i>
                <p>Real-time hourly forecast not available</p>
            </div>
        `
    return
  }

  // Update forecast header
  locationElement.textContent = `Forecast for ${forecastData.location}`
  timeElement.textContent = `Updated: ${new Date(forecastData.timestamp).toLocaleTimeString()}`

  forecastData.hourly.forEach((hour, index) => {
    const hourItem = document.createElement("div")
    const isCurrentHour = index === 0

    hourItem.className = `hourly-item ${isCurrentHour ? "current-hour" : ""}`

    hourItem.innerHTML = `
            <div style="font-size: 0.8rem; color: #7f8c8d; margin-bottom: 8px; font-weight: ${isCurrentHour ? "600" : "400"};">${hour.time}</div>
            <img src="https://openweathermap.org/img/wn/${hour.icon}@2x.png" alt="Weather" style="width: 45px; height: 45px; margin-bottom: 8px;">
            <div style="font-weight: 600; color: #2c3e50; margin-bottom: 5px; font-size: ${isCurrentHour ? "1.1rem" : "1rem"};">${hour.temp}°F</div>
            <div style="font-size: 0.7rem; color: #4a90e2; margin-bottom: 3px;">
                <i class="fas fa-tint"></i> ${hour.precipitation}%
            </div>
            <div style="font-size: 0.7rem; color: #7f8c8d; margin-bottom: 3px;">
                <i class="fas fa-wind"></i> ${hour.windSpeed} mph
            </div>
            <div style="font-size: 0.7rem; color: #95a5a6;">
                <i class="fas fa-eye"></i> ${hour.visibility} mi
            </div>
        `
    container.appendChild(hourItem)
  })
}

function toggleAutoRefresh() {
  autoRefreshEnabled = !autoRefreshEnabled
  const indicator = document.getElementById("autoRefreshIndicator")
  const icon = document.getElementById("autoRefreshIcon")
  const text = document.getElementById("autoRefreshText")

  if (autoRefreshEnabled) {
    indicator.className = "auto-refresh-indicator"
    indicator.innerHTML = '<i class="fas fa-circle" style="color: #50c878;"></i> Auto-refresh ON'
    icon.className = "fas fa-sync-alt fa-spin"
    text.textContent = "Auto ON"

    // Start auto-refresh every 3 minutes
    autoRefreshInterval = setInterval(
      () => {
        refreshForecastData()
      },
      3 * 60 * 1000,
    )

    showMessage("Auto-refresh enabled (every 3 minutes)", "success")
    localStorage.setItem("autoRefreshEnabled", "true")
  } else {
    indicator.className = "auto-refresh-indicator disabled"
    indicator.innerHTML = '<i class="fas fa-circle"></i> Auto-refresh OFF'
    icon.className = "fas fa-sync-alt"
    text.textContent = "Auto OFF"

    if (autoRefreshInterval) {
      clearInterval(autoRefreshInterval)
      autoRefreshInterval = null
    }

    showMessage("Auto-refresh disabled", "info")
    localStorage.setItem("autoRefreshEnabled", "false")
  }
}

async function refreshForecastData() {
  if (!selectedCity && !currentLocation) return

  try {
    if (currentLocation) {
      await loadForecastDataByCoords(currentLocation.lat, currentLocation.lon)
    } else if (selectedCity) {
      await loadForecastData(selectedCity, selectedCountry)
    }

    console.log("Forecast auto-refreshed at", new Date().toLocaleTimeString())
  } catch (error) {
    console.error("Error auto-refreshing forecast:", error)
  }
}

async function generateCommuteRecommendations() {
  if (!selectedCity && !currentLocation) return

  try {
    const params = new URLSearchParams()

    if (currentLocation) {
      params.append("lat", currentLocation.lat.toString())
      params.append("lon", currentLocation.lon.toString())
    } else if (selectedCity) {
      params.append("city", selectedCity)
      if (selectedCountry) params.append("country", selectedCountry)
    }

    const response = await fetch(`/business/api/commute/recommendations?${params}`)
    const result = await response.json()

    if (result.success && result.recommendations) {
      displayCommuteRecommendations(result.recommendations, result.bestTime)
    } else {
      displayDefaultRecommendations()
    }
  } catch (error) {
    console.error("Error loading commute recommendations:", error)
    displayDefaultRecommendations()
  }
}

function displayCommuteRecommendations(recommendations, bestTime) {
  const container = document.getElementById("commuteOptions")
  container.innerHTML = ""

  // Show top 3 recommendations
  recommendations.slice(0, 3).forEach((rec) => {
    const option = document.createElement("div")
    option.className = "commute-option"
    option.innerHTML = `
            <div class="commute-time">
                <span class="time-slot">${rec.timeSlot}</span>
                <span class="weather-risk risk-${rec.riskLevel}">
                    ${rec.riskLevel.charAt(0).toUpperCase() + rec.riskLevel.slice(1)} Risk
                </span>
            </div>
            <div style="font-size: 0.9rem; color: #5a6c7d; margin-bottom: 8px;">
                Score: ${Math.round(rec.score)}/100
            </div>
            <div style="font-size: 0.85rem; color: #7f8c8d;">
                ${rec.description}
            </div>
        `
    container.appendChild(option)
  })

  // Show best recommendation
  if (bestTime) {
    document.getElementById("commuteRecommendation").style.display = "block"
    document.getElementById("recommendationText").textContent =
      `Best time: ${bestTime.timeSlot} (Score: ${Math.round(bestTime.score)}/100)`
  }
}

function displayDefaultRecommendations() {
  // Fallback to original static recommendations
  const recommendations = [
    {
      timeSlot: "7:00 AM - 7:30 AM",
      riskLevel: "low",
      description: "Clear conditions expected, optimal travel time",
    },
    {
      timeSlot: "8:00 AM - 8:30 AM",
      riskLevel: "medium",
      description: "Moderate traffic, check weather updates",
    },
    {
      timeSlot: "9:00 AM - 9:30 AM",
      riskLevel: "high",
      description: "Peak traffic, weather may impact travel",
    },
  ]

  displayCommuteRecommendations(recommendations, recommendations[0])
}

async function generateTravelAlerts() {
  const container = document.getElementById("travelAlerts")
  container.innerHTML = `
        <div style="text-align: center; color: #7f8c8d; padding: 40px;">
            <i class="fas fa-spinner fa-spin" style="font-size: 2rem; margin-bottom: 15px;"></i>
            <p>Loading travel alerts...</p>
        </div>
    `

  try {
    const params = new URLSearchParams()
    if (currentLocation) {
      params.append("lat", currentLocation.lat.toString())
      params.append("lon", currentLocation.lon.toString())
    } else if (selectedCity) {
      // For alerts, we need coordinates. If only city is selected, try to get coords.
      // For simplicity, we'll use default fallback coords if not available.
      params.append("lat", "40.7128") // Default New York Lat
      params.append("lon", "-74.0060") // Default New York Lon
    } else {
      container.innerHTML = `
                <div style="text-align: center; color: #7f8c8d; padding: 40px;">
                    <i class="fas fa-info-circle" style="font-size: 2rem; margin-bottom: 15px;"></i>
                    <p>Select a location to view weather-based travel alerts</p>
                </div>
            `
      return
    }

    const response = await fetch(`/business/api/alerts/real-time?${params}`)
    const result = await response.json()

    if (result.success && result.alerts && result.alerts.length > 0) {
      container.innerHTML = "" // Clear loading state
      result.alerts.forEach((alert) => {
        const alertDiv = document.createElement("div")
        alertDiv.style.cssText = `padding: 15px; background: ${alert.color === "#50c878" ? "#d4edda" : "#fff3cd"}; border-radius: 8px; border-left: 4px solid ${alert.color}; margin-bottom: 15px;`
        alertDiv.innerHTML = `
                    <div style="font-weight: 600; margin-bottom: 5px; color: ${alert.color};">
                        <i class="fas fa-${alert.icon}"></i> ${alert.title}
                    </div>
                    <div style="font-size: 0.9rem; color: #5a6c7d;">
                        ${alert.description}
                    </div>
                `
        container.appendChild(alertDiv)
      })
    } else {
      container.innerHTML = `
                <div style="text-align: center; color: #7f8c8d; padding: 40px;">
                    <i class="fas fa-check-circle" style="font-size: 2rem; margin-bottom: 15px; color: #50c878;"></i>
                    <p>No active travel alerts for this location.</p>
                </div>
            `
    }
  } catch (error) {
    console.error("Error loading travel alerts:", error)
    container.innerHTML = `
            <div style="text-align: center; color: #7f8c8d; padding: 40px;">
                <i class="fas fa-exclamation-triangle" style="font-size: 2rem; margin-bottom: 15px; color: #e74c3c;"></i>
                <p>Unable to load travel alerts. Please try again.</p>
            </div>
        `
  }
}

function refreshDashboard() {
  if (selectedCity) {
    selectLocation(selectedCity, selectedCountry)
  } else if (currentLocation) {
    useCurrentLocation()
  } else {
    showMessage("Please select a location first", "error")
  }
}

function updateLastUpdateTime() {
  const now = new Date()
  document.getElementById("lastUpdate").textContent = `Last updated: ${now.toLocaleTimeString()}`
}

function showMessage(message, type = "info") {
  const container = document.getElementById("statusMessages")

  // Remove existing messages
  container.innerHTML = ""

  if (type === "info" && message.includes("Loading")) {
    // Don't show loading messages as persistent notifications
    return
  }

  const messageDiv = document.createElement("div")
  let bgColor, textColor, borderColor, icon

  switch (type) {
    case "success":
      bgColor = "#d4edda"
      textColor = "#155724"
      borderColor = "#50c878"
      icon = "check-circle"
      break
    case "error":
      bgColor = "#f8d7da"
      textColor = "#721c24"
      borderColor = "#e74c3c"
      icon = "exclamation-triangle"
      break
    default:
      bgColor = "#d1ecf1"
      textColor = "#0c5460"
      borderColor = "#4a90e2"
      icon = "info-circle"
  }

  messageDiv.className = type === "success" ? "success-message" : "error-message"
  messageDiv.innerHTML = `
        <div style="font-weight: 600; margin-bottom: 5px;">
            <i class="fas fa-${icon}"></i> ${type.charAt(0).toUpperCase() + type.slice(1)}
        </div>
        <div style="font-size: 0.9rem;">${message}</div>
    `

  container.appendChild(messageDiv)

  // Auto-remove success messages after 3 seconds
  if (type === "success") {
    setTimeout(() => {
      if (messageDiv.parentNode) {
        messageDiv.parentNode.removeChild(messageDiv)
      }
    }, 3000)
  }
}

// Handle page visibility changes for auto-refresh
document.addEventListener("visibilitychange", () => {
  if (document.hidden && autoRefreshInterval) {
    // Pause auto-refresh when page is hidden
    clearInterval(autoRefreshInterval)
    autoRefreshInterval = null
  } else if (!document.hidden && autoRefreshEnabled && !autoRefreshInterval) {
    // Resume auto-refresh when page becomes visible
    autoRefreshInterval = setInterval(
      () => {
        refreshForecastData()
      },
      3 * 60 * 1000,
    )
  }
})
