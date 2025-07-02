// Global variables
let currentLocation = null
let selectedCity = "Sehore"
let selectedCountry = "IN"
let autoRefreshInterval = null
let isAutoRefreshEnabled = false

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
  console.log("Page loaded, initializing...")

  // Initialize sidebar functionality
  initializeSidebar()

  // Initialize hourly forecast functionality
  initializeHourlyForecast()

  // Load initial forecast for Sehore
  loadHourlyForecast()

  // Update forecast location name
  updateForecastLocationName("Sehore", "IN")
})

function initializeSidebar() {
  const menuBtn = document.getElementById("menuBtn")
  const sidebar = document.getElementById("sidebar")
  const sidebarOverlay = document.getElementById("sidebarOverlay")
  const closeSidebar = document.getElementById("closeSidebar")
  const searchToggle = document.getElementById("searchToggle")
  const locationSearch = document.getElementById("locationSearch")
  const searchBtn = document.getElementById("searchBtn")

  // Open sidebar
  function openSidebar() {
    sidebar.classList.add("active")
    sidebarOverlay.classList.add("active")
    document.body.style.overflow = "hidden"
  }

  // Close sidebar
  function closeSidebarFunc() {
    sidebar.classList.remove("active")
    sidebarOverlay.classList.remove("active")
    document.body.style.overflow = "auto"
  }

  // Event listeners
  if (menuBtn) menuBtn.addEventListener("click", openSidebar)
  if (searchToggle) searchToggle.addEventListener("click", openSidebar)
  if (closeSidebar) closeSidebar.addEventListener("click", closeSidebarFunc)
  if (sidebarOverlay) sidebarOverlay.addEventListener("click", closeSidebarFunc)

  // Search functionality
  if (locationSearch) {
    locationSearch.addEventListener("keypress", (e) => {
      if (e.key === "Enter") {
        performLocationSearch()
      }
    })
  }

  if (searchBtn) {
    searchBtn.addEventListener("click", performLocationSearch)
  }

  // Handle city button clicks
  const cityButtons = document.querySelectorAll(".city-btn")
  cityButtons.forEach((button) => {
    button.addEventListener("click", function () {
      const city = this.getAttribute("data-city")
      const country = this.getAttribute("data-country")
      selectLocation(city, country)
      closeSidebarFunc()
    })
  })

  // Handle recent location clicks
  const recentItems = document.querySelectorAll(".recent-location-item")
  recentItems.forEach((item) => {
    item.addEventListener("click", function () {
      const city = this.getAttribute("data-city")
      const country = this.getAttribute("data-country")
      selectLocation(city, country)
      closeSidebarFunc()
    })
  })

  // Handle current location button
  const currentLocationBtn = document.getElementById("useCurrentLocation")
  if (currentLocationBtn) {
    currentLocationBtn.addEventListener("click", () => {
      useCurrentLocation()
      closeSidebarFunc()
    })
  }

  // Close sidebar on escape key
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      closeSidebarFunc()
    }
  })
}

function initializeHourlyForecast() {
  const refreshToggle = document.getElementById("refreshToggle")
  const refreshStatus = document.getElementById("refreshStatus")

  if (refreshToggle) {
    refreshToggle.addEventListener("click", () => {
      toggleAutoRefresh()
    })
  }
}

function toggleAutoRefresh() {
  const refreshToggle = document.getElementById("refreshToggle")
  const refreshStatus = document.getElementById("refreshStatus")

  isAutoRefreshEnabled = !isAutoRefreshEnabled

  if (isAutoRefreshEnabled) {
    refreshToggle.classList.add("active")
    refreshStatus.textContent = "ON"
    startAutoRefresh()
    showMessage("Auto-refresh enabled", "success")
  } else {
    refreshToggle.classList.remove("active")
    refreshStatus.textContent = "OFF"
    stopAutoRefresh()
    showMessage("Auto-refresh disabled", "info")
  }
}

function startAutoRefresh() {
  if (autoRefreshInterval) {
    clearInterval(autoRefreshInterval)
  }

  autoRefreshInterval = setInterval(() => {
    console.log("Auto-refreshing forecast data...")
    loadHourlyForecast()
  }, 180000) // 3 minutes
}

function stopAutoRefresh() {
  if (autoRefreshInterval) {
    clearInterval(autoRefreshInterval)
    autoRefreshInterval = null
  }
}

function performLocationSearch() {
  const input = document.getElementById("locationSearch").value.trim()
  if (!input) {
    showMessage("Please enter a city name", "error")
    return
  }

  const parts = input.split(",")
  const city = parts[0].trim()
  const country = parts[1] ? parts[1].trim() : ""

  selectLocation(city, country)
  document.getElementById("locationSearch").value = ""
}

async function selectLocation(city, country = "") {
  selectedCity = city
  selectedCountry = country

  console.log(`Selecting location: ${city}, ${country}`)
  showMessage(`Loading weather data for ${city}...`, "info")

  try {
    await loadHourlyForecast()
    updateForecastLocationName(city, country)
    showMessage(`Weather data loaded for ${city}`, "success")
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
        await loadHourlyForecastByCoords(currentLocation.lat, currentLocation.lon)
        updateForecastLocationName("Current Location")
        showMessage("Weather data loaded for your current location", "success")
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

function updateForecastLocationName(city, country = "") {
  const locationElement = document.getElementById("forecastLocationName")
  if (locationElement) {
    const locationText = country ? `${city}, ${country}` : city
    locationElement.textContent = locationText
    console.log(`Updated forecast location to: ${locationText}`)
  }
}

function updateMainWeatherDisplay(weatherData, locationData) {
  console.log("Updating main weather display:", weatherData, locationData)

  // Update city name and country
  const cityNameElements = document.querySelectorAll(".city-name span")
  if (cityNameElements.length >= 2) {
    cityNameElements[0].textContent = locationData.cityName || selectedCity
    cityNameElements[1].textContent = locationData.country || selectedCountry
  }

  // Update current time
  const currentTimeElement = document.querySelector(".current-time")
  if (currentTimeElement) {
    const now = new Date()
    const timeString = now.toLocaleDateString("en-US", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    })
    currentTimeElement.textContent = timeString
  }

  // Update main temperature
  const tempElement = document.querySelector(".temperature-main")
  if (tempElement && weatherData.current) {
    tempElement.textContent = `${weatherData.current.temp}°C`
  }

  // Update feels like temperature
  const feelsLikeElement = document.querySelector(".feels-like span")
  if (feelsLikeElement && weatherData.current) {
    feelsLikeElement.textContent = `${weatherData.current.feelsLike}°C`
  }

  // Update weather description
  const descriptionElement = document.querySelector(".weather-description")
  if (descriptionElement && weatherData.current) {
    descriptionElement.textContent = weatherData.current.description
  }

  // Update weather main text
  const mainTextElement = document.querySelector(".weather-main-text")
  if (mainTextElement && weatherData.current) {
    mainTextElement.textContent = weatherData.current.main
  }

  // Update weather icon
  const iconElement = document.querySelector(".weather-icon-large")
  if (iconElement && weatherData.current) {
    iconElement.src = `https://openweathermap.org/img/wn/${weatherData.current.icon}@2x.png`
    iconElement.alt = weatherData.current.description
  }

  // Update weather details
  if (weatherData.current) {
    updateWeatherDetail("visibility", `${weatherData.current.visibility} km`)
    updateWeatherDetail("humidity", `${weatherData.current.humidity}%`)
    updateWeatherDetail("pressure", `${weatherData.current.pressure} hPa`)
    updateWeatherDetail("wind-speed", `${weatherData.current.windSpeed} km/h`)
    updateWeatherDetail("uv-index", weatherData.current.uvIndex)
    updateWeatherDetail("wind-direction", `${weatherData.current.windDirection}°`)
  }
}

function updateWeatherDetail(type, value) {
  const detailCards = document.querySelectorAll(".detail-card")
  detailCards.forEach((card) => {
    const label = card.querySelector(".detail-label").textContent.toLowerCase()
    if (
      (type === "visibility" && label.includes("visibility")) ||
      (type === "humidity" && label.includes("humidity")) ||
      (type === "pressure" && label.includes("pressure")) ||
      (type === "wind-speed" && label.includes("wind speed")) ||
      (type === "uv-index" && label.includes("uv index")) ||
      (type === "wind-direction" && label.includes("wind direction"))
    ) {
      const valueElement = card.querySelector(".detail-value")
      if (valueElement) {
        valueElement.textContent = value
      }
    }
  })
}

async function loadHourlyForecast() {
  console.log(`Loading forecast for: ${selectedCity}, ${selectedCountry}`)

  showLoadingState()

  try {
    const params = new URLSearchParams()
    if (selectedCity) params.append("city", selectedCity)
    if (selectedCountry) params.append("country", selectedCountry)

    console.log(`API URL: /business/api/weather/forecast?${params}`)

    // Load both forecast and current weather data
    const [forecastResponse, currentResponse] = await Promise.all([
      fetch(`/business/api/weather/forecast?${params}`),
      fetch(`/business/api/weather/current?${params}`),
    ])

    if (!forecastResponse.ok || !currentResponse.ok) {
      throw new Error(`HTTP error! forecast: ${forecastResponse.status}, current: ${currentResponse.status}`)
    }

    const forecastResult = await forecastResponse.json()
    const currentResult = await currentResponse.json()

    console.log("Forecast API Response:", forecastResult)
    console.log("Current Weather API Response:", currentResult)

    if (forecastResult.success && forecastResult.data && forecastResult.data.hourly) {
      displayHourlyForecast(forecastResult.data.hourly)
    } else {
      console.error("Invalid forecast API response:", forecastResult)
      throw new Error(forecastResult.error || "Forecast data not available")
    }

    // Update main weather display if current weather data is available
    if (currentResult.success && currentResult.data) {
      updateMainWeatherDisplay(currentResult.data, {
        cityName: selectedCity,
        country: selectedCountry,
      })
    }
  } catch (error) {
    console.error("Error loading weather data:", error)
    showErrorState("Unable to load weather data")
  }
}

async function loadHourlyForecastByCoords(lat, lon) {
  console.log(`Loading forecast for coordinates: ${lat}, ${lon}`)

  showLoadingState()

  try {
    const params = new URLSearchParams()
    params.append("lat", lat.toString())
    params.append("lon", lon.toString())

    // Load both forecast and current weather data
    const [forecastResponse, currentResponse] = await Promise.all([
      fetch(`/business/api/weather/forecast?${params}`),
      fetch(`/business/api/weather/current?${params}`),
    ])

    if (!forecastResponse.ok || !currentResponse.ok) {
      throw new Error(`HTTP error! forecast: ${forecastResponse.status}, current: ${currentResponse.status}`)
    }

    const forecastResult = await forecastResponse.json()
    const currentResult = await currentResponse.json()

    console.log("Forecast API Response:", forecastResult)
    console.log("Current Weather API Response:", currentResult)

    if (forecastResult.success && forecastResult.data && forecastResult.data.hourly) {
      displayHourlyForecast(forecastResult.data.hourly)
    } else {
      throw new Error(forecastResult.error || "Forecast data not available")
    }

    // Update main weather display if current weather data is available
    if (currentResult.success && currentResult.data) {
      updateMainWeatherDisplay(currentResult.data, {
        cityName: "Current Location",
        country: "",
      })
    }
  } catch (error) {
    console.error("Error loading weather data by coords:", error)
    showErrorState("Unable to load weather data")
  }
}

function showLoadingState() {
  const container = document.getElementById("hourlyForecastContainer")
  container.innerHTML = `
        <div class="loading-state">
            <i class="fas fa-spinner"></i>
            <p>Loading forecast data...</p>
        </div>
    `
}

function showErrorState(message) {
  const container = document.getElementById("hourlyForecastContainer")
  container.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-exclamation-triangle"></i>
            <p>${message}</p>
        </div>
    `
}

function displayHourlyForecast(hourlyData) {
  console.log("Displaying hourly forecast:", hourlyData)

  const container = document.getElementById("hourlyForecastContainer")

  if (!hourlyData || hourlyData.length === 0) {
    showErrorState("No forecast data available")
    return
  }

  let html = ""

  hourlyData.forEach((hour, index) => {
    const isCurrentHour = hour.time === "NOW"
    const currentHourClass = isCurrentHour ? "current-hour" : ""

    html += `
            <div class="hourly-item ${currentHourClass}">
                <div class="hourly-time">${hour.time}</div>
                <img src="https://openweathermap.org/img/wn/${hour.icon}@2x.png" alt="Weather" class="hourly-icon">
                <div class="hourly-temp">${hour.temp}°C</div>
                <div class="hourly-details">
                    <div class="hourly-detail-item">
                        <i class="fas fa-cloud-rain"></i>
                        <span>${hour.precipitation}%</span>
                    </div>
                    <div class="hourly-detail-item">
                        <i class="fas fa-wind"></i>
                        <span>${hour.windSpeed} km/h</span>
                    </div>
                    <div class="hourly-detail-item">
                        <i class="fas fa-eye"></i>
                        <span>${hour.visibility} km</span>
                    </div>
                </div>
            </div>
        `
  })

  container.innerHTML = html
  console.log("Hourly forecast displayed successfully")
}

function refreshWeather() {
  console.log("Refreshing weather data...")
  if (selectedCity) {
    selectLocation(selectedCity, selectedCountry)
  } else if (currentLocation) {
    // Call loadHourlyForecastByCoords directly instead of useCurrentLocation
    loadHourlyForecastByCoords(currentLocation.lat, currentLocation.lon)
  } else {
    showMessage("Please select a location first", "error")
  }
}

function showMessage(message, type = "info") {
  console.log(`Message: ${message} (${type})`)

  // Remove existing toasts
  const existingToasts = document.querySelectorAll(".message-toast")
  existingToasts.forEach((toast) => toast.remove())

  // Create new toast
  const toast = document.createElement("div")
  toast.className = `message-toast ${type}`
  toast.textContent = message

  document.body.appendChild(toast)

  // Show toast
  setTimeout(() => {
    toast.classList.add("show")
  }, 100)

  // Hide and remove toast
  setTimeout(() => {
    toast.classList.remove("show")
    setTimeout(() => {
      if (toast.parentNode) {
        toast.parentNode.removeChild(toast)
      }
    }, 300)
  }, 3000)
}

// Cleanup on page unload
window.addEventListener("beforeunload", () => {
  stopAutoRefresh()
})

// Initialize forecast on page load
window.addEventListener("load", () => {
  console.log("Window loaded, initializing forecast...")
  setTimeout(() => {
    loadHourlyForecast()
  }, 1000)
})
