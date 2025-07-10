// Global variables
let currentLocation = null
let selectedCity = "Sehore"
let selectedCountry = "IN"
let autoRefreshInterval = null
let isAutoRefreshEnabled = false

// Autocomplete variables
let autocompleteTimeout = null
const autocompleteCache = new Map()

// Initialize on page load
document.addEventListener("DOMContentLoaded", () => {
  console.log("Page loaded, initializing...")

  // Initialize location from URL parameters first
  initializeLocationFromURL()

  // Initialize sidebar functionality
  initializeSidebar()

  // Initialize autocomplete functionality
  initializeAutocomplete()

  // Initialize hourly forecast functionality
  initializeHourlyForecast()

  // Load initial forecast for current location
  loadHourlyForecast()
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

  // Update forecast location name immediately
  updateForecastLocationName(city, country)

  showMessage(`Loading weather data for ${city}...`, "info")

  try {
    // Redirect to the page with new city parameters to get fresh server-side data
    window.location.href = `/business/weather-detailed?city=${encodeURIComponent(city)}&country=${encodeURIComponent(country)}`
  } catch (error) {
    console.error("Error loading location data:", error)
    showMessage(`Unable to load weather data for ${city}. Please try another location.`, "error")
  }
}

function initializeLocationFromURL() {
  const urlParams = new URLSearchParams(window.location.search)
  const cityFromURL = urlParams.get("city")
  const countryFromURL = urlParams.get("country")

  if (cityFromURL) {
    selectedCity = cityFromURL
    selectedCountry = countryFromURL || ""

    console.log(`Location from URL: ${selectedCity}, ${selectedCountry}`)
    updateForecastLocationName(selectedCity, selectedCountry)
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

async function loadHourlyForecast() {
  console.log(`Loading forecast for: ${selectedCity}, ${selectedCountry}`)

  // Make sure forecast location name is updated
  updateForecastLocationName(selectedCity, selectedCountry)

  showLoadingState()

  try {
    const params = new URLSearchParams()
    if (selectedCity) params.append("city", selectedCity)
    if (selectedCountry) params.append("country", selectedCountry)

    const apiUrl = `/business/api/weather/forecast?${params}`
    console.log(`API URL: ${apiUrl}`)

    const forecastResponse = await fetch(apiUrl)

    console.log(`Response status: ${forecastResponse.status}`)

    if (!forecastResponse.ok) {
      throw new Error(`HTTP error! status: ${forecastResponse.status}`)
    }

    const forecastResult = await forecastResponse.json()
    console.log("Forecast API Response:", forecastResult)

    if (forecastResult.success && forecastResult.data && forecastResult.data.hourly) {
      if (forecastResult.fallback) {
        console.log("⚠️ Using fallback forecast data")
        showMessage("Using sample forecast data - API may be unavailable", "info")
      }
      displayHourlyForecast(forecastResult.data.hourly)

      // Update location name from API response if available
      if (forecastResult.data.location) {
        updateForecastLocationName(forecastResult.data.location)
      }
    } else {
      console.error("Invalid forecast API response:", forecastResult)
      throw new Error(forecastResult.error || "Forecast data not available")
    }
  } catch (error) {
    console.error("Error loading weather data:", error)
    showErrorState(`Unable to load weather data: ${error.message}`)
    showMessage("Failed to load hourly forecast", "error")
  }
}

async function loadHourlyForecastByCoords(lat, lon) {
  console.log(`Loading forecast for coordinates: ${lat}, ${lon}`)

  showLoadingState()

  try {
    const params = new URLSearchParams()
    params.append("lat", lat.toString())
    params.append("lon", lon.toString())

    const forecastResponse = await fetch(`/business/api/weather/forecast?${params}`)

    if (!forecastResponse.ok) {
      throw new Error(`HTTP error! forecast: ${forecastResponse.status}`)
    }

    const forecastResult = await forecastResponse.json()

    console.log("Forecast API Response:", forecastResult)

    if (forecastResult.success && forecastResult.data && forecastResult.data.hourly) {
      displayHourlyForecast(forecastResult.data.hourly)
    } else {
      throw new Error(forecastResult.error || "Forecast data not available")
    }
  } catch (error) {
    console.error("Error loading weather data by coords:", error)
    showErrorState("Unable to load weather data")
  }
}

function showLoadingState() {
  const container = document.getElementById("hourlyForecastContainer")
  if (container) {
    container.innerHTML = `
        <div class="loading-state">
            <i class="fas fa-spinner"></i>
            <p>Loading real-time forecast data...</p>
        </div>
    `
  }
}

function showErrorState(message) {
  const container = document.getElementById("hourlyForecastContainer")
  if (container) {
    container.innerHTML = `
        <div class="empty-state">
            <i class="fas fa-exclamation-triangle"></i>
            <p>${message}</p>
        </div>
    `
  }
}

function displayHourlyForecast(hourlyData) {
  console.log("Displaying hourly forecast:", hourlyData)

  const container = document.getElementById("hourlyForecastContainer")
  if (!container) {
    console.error("Hourly forecast container not found!")
    return
  }

  if (!hourlyData || hourlyData.length === 0) {
    showErrorState("No forecast data available")
    return
  }

  let html = ""

  hourlyData.forEach((hour, index) => {
    const isCurrentHour = hour.time === "Now"
    const currentHourClass = isCurrentHour ? "current-hour" : ""

    html += `
            <div class="hourly-item ${currentHourClass}">
                <div class="hourly-time">${hour.time}</div>
                <img src="https://openweathermap.org/img/wn/${hour.icon}@2x.png"
                     alt="Weather"
                     class="hourly-icon"
                     onerror="this.src='https://openweathermap.org/img/wn/01d@2x.png'">
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
  console.log(`✅ Hourly forecast displayed successfully with ${hourlyData.length} items`)
}

function refreshWeather() {
  console.log("Refreshing weather data...")
  // Reload the page to get fresh data from server
  window.location.reload()
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

function initializeAutocomplete() {
  const locationSearch = document.getElementById("locationSearch")
  const searchContainer = locationSearch?.parentElement

  if (!locationSearch || !searchContainer) return

  // Create autocomplete dropdown
  const dropdown = document.createElement("div")
  dropdown.className = "autocomplete-dropdown"
  dropdown.id = "autocompleteDropdown"

  // Make search container relative for positioning
  searchContainer.classList.add("search-autocomplete")
  searchContainer.appendChild(dropdown)

  // Add input event listener
  locationSearch.addEventListener("input", handleAutocompleteInput)

  // Hide dropdown when clicking outside
  document.addEventListener("click", (e) => {
    if (!searchContainer.contains(e.target)) {
      hideAutocomplete()
    }
  })

  // Handle escape key
  locationSearch.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      hideAutocomplete()
    }
  })
}

function handleAutocompleteInput(e) {
  const query = e.target.value.trim()

  // Clear existing timeout
  if (autocompleteTimeout) {
    clearTimeout(autocompleteTimeout)
  }

  if (query.length < 2) {
    hideAutocomplete()
    return
  }

  // Show loading state
  showAutocompleteLoading()

  // Debounce API calls
  autocompleteTimeout = setTimeout(() => {
    fetchCitySuggestions(query)
  }, 300)
}

async function fetchCitySuggestions(query) {
  try {
    // Check cache first
    const cacheKey = query.toLowerCase()
    if (autocompleteCache.has(cacheKey)) {
      displayAutocompleteSuggestions(autocompleteCache.get(cacheKey))
      return
    }

    console.log(`Fetching city suggestions for: ${query}`)

    // Use OpenWeatherMap Geocoding API
    const response = await fetch(
      `http://api.openweathermap.org/geo/1.0/direct?q=${encodeURIComponent(query)}&limit=8&appid=61457956659eec8e4068cf5aecf8f337`,
    )

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const cities = await response.json()

    // Cache the results
    autocompleteCache.set(cacheKey, cities)

    displayAutocompleteSuggestions(cities)
  } catch (error) {
    console.error("Error fetching city suggestions:", error)
    showAutocompleteError()
  }
}

function displayAutocompleteSuggestions(cities) {
  const dropdown = document.getElementById("autocompleteDropdown")
  if (!dropdown) return

  if (!cities || cities.length === 0) {
    dropdown.innerHTML = `
      <div class="autocomplete-no-results">
        <i class="fas fa-search"></i>
        No cities found. Try a different search term.
      </div>
    `
    dropdown.classList.add("show")
    return
  }

  let html = ""

  cities.forEach((city) => {
    const cityName = city.name
    const country = city.country
    const state = city.state ? `, ${city.state}` : ""
    const lat = city.lat?.toFixed(2) || ""
    const lon = city.lon?.toFixed(2) || ""

    html += `
      <div class="autocomplete-item"
           data-city="${cityName}"
           data-country="${country}"
           data-lat="${city.lat}"
           data-lon="${city.lon}">
        <i class="fas fa-map-marker-alt"></i>
        <div class="autocomplete-city-info">
          <div class="autocomplete-city-name">${cityName}${state}</div>
          <div class="autocomplete-city-details">${country} • ${lat}, ${lon}</div>
        </div>
      </div>
    `
  })

  dropdown.innerHTML = html
  dropdown.classList.add("show")

  // Add click listeners to suggestions
  dropdown.querySelectorAll(".autocomplete-item").forEach((item) => {
    item.addEventListener("click", () => {
      const city = item.getAttribute("data-city")
      const country = item.getAttribute("data-country")

      // Fill the input
      document.getElementById("locationSearch").value = `${city}, ${country}`

      // Hide dropdown
      hideAutocomplete()

      // Select the location
      selectLocation(city, country)
    })
  })
}

function showAutocompleteLoading() {
  const dropdown = document.getElementById("autocompleteDropdown")
  if (!dropdown) return

  dropdown.innerHTML = `
    <div class="autocomplete-loading">
      <i class="fas fa-spinner"></i>
      Searching cities...
    </div>
  `
  dropdown.classList.add("show")
}

function showAutocompleteError() {
  const dropdown = document.getElementById("autocompleteDropdown")
  if (!dropdown) return

  dropdown.innerHTML = `
    <div class="autocomplete-no-results">
      <i class="fas fa-exclamation-triangle"></i>
      Unable to search cities. Please try again.
    </div>
  `
  dropdown.classList.add("show")
}

function hideAutocomplete() {
  const dropdown = document.getElementById("autocompleteDropdown")
  if (dropdown) {
    dropdown.classList.remove("show")
  }
}

// Manual test function for debugging
function testHourlyForecast() {
  console.log("🧪 Testing hourly forecast manually...")
  loadHourlyForecast()
}

// Make it available globally for debugging
window.testHourlyForecast = testHourlyForecast
