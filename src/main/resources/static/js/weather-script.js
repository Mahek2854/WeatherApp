// Weather Dashboard JavaScript - FIXED VERSION

// DOM Elements
const sidebar = document.getElementById("sidebar")
const sidebarOverlay = document.getElementById("sidebarOverlay")
const menuBtn = document.getElementById("menuBtn")
const closeSidebar = document.getElementById("closeSidebar")
const searchToggle = document.getElementById("searchToggle")
const locationSearch = document.getElementById("locationSearch")
const searchBtn = document.getElementById("searchBtn")
const searchSuggestions = document.getElementById("searchSuggestions")
const useCurrentLocation = document.getElementById("useCurrentLocation")
const recentLocationsList = document.getElementById("recentLocationsList")

// Sidebar functionality
function openSidebar() {
  sidebar.classList.add("active")
  sidebarOverlay.classList.add("active")
  document.body.style.overflow = "hidden"
}

function closeSidebarFunc() {
  sidebar.classList.remove("active")
  sidebarOverlay.classList.remove("active")
  document.body.style.overflow = "auto"
}

// Event Listeners
if (menuBtn) menuBtn.addEventListener("click", openSidebar)
if (searchToggle) searchToggle.addEventListener("click", openSidebar)
if (closeSidebar) closeSidebar.addEventListener("click", closeSidebarFunc)
if (sidebarOverlay) sidebarOverlay.addEventListener("click", closeSidebarFunc)

// Search functionality
let searchTimeout
if (locationSearch) {
  locationSearch.addEventListener("input", function () {
    clearTimeout(searchTimeout)
    const query = this.value.trim()

    if (query.length < 2) {
      if (searchSuggestions) searchSuggestions.classList.remove("active")
      return
    }

    searchTimeout = setTimeout(() => {
      searchLocations(query)
    }, 300)
  })
}

if (searchBtn) {
  searchBtn.addEventListener("click", () => {
    const query = locationSearch.value.trim()
    if (query) {
      searchAndNavigate(query)
    }
  })
}

if (locationSearch) {
  locationSearch.addEventListener("keypress", function (e) {
    if (e.key === "Enter") {
      const query = this.value.trim()
      if (query) {
        searchAndNavigate(query)
      }
    }
  })
}

// Search locations function
async function searchLocations(query) {
  try {
    const response = await fetch(`/search/suggestions?query=${encodeURIComponent(query)}`)
    const locations = await response.json()

    displaySearchSuggestions(locations)
  } catch (error) {
    console.error("Error searching locations:", error)
    // Show fallback suggestions
    displayFallbackSuggestions(query)
  }
}

// Display fallback suggestions when API fails
function displayFallbackSuggestions(query) {
  const fallbackSuggestions = [
    { cityName: "New York", country: "US" },
    { cityName: "London", country: "GB" },
    { cityName: "Tokyo", country: "JP" },
    { cityName: "Paris", country: "FR" },
    { cityName: "Sydney", country: "AU" },
    { cityName: "Dubai", country: "AE" },
  ]

  const filteredSuggestions = fallbackSuggestions.filter((location) =>
    location.cityName.toLowerCase().includes(query.toLowerCase()),
  )

  displaySearchSuggestions(filteredSuggestions)
}

// Display search suggestions
function displaySearchSuggestions(locations) {
  if (!searchSuggestions) return

  searchSuggestions.innerHTML = ""

  if (locations.length === 0) {
    searchSuggestions.innerHTML = '<div class="suggestion-item">No locations found</div>'
    searchSuggestions.classList.add("active")
    return
  }

  locations.forEach((location) => {
    const suggestionItem = document.createElement("div")
    suggestionItem.className = "suggestion-item"
    suggestionItem.innerHTML = `
            <i class="fas fa-map-marker-alt"></i>
            ${location.cityName || location.city}, ${location.country}
            ${location.state ? `, ${location.state}` : ""}
        `

    suggestionItem.addEventListener("click", () => {
      navigateToLocation(location.cityName || location.city, location.country)
    })

    searchSuggestions.appendChild(suggestionItem)
  })

  searchSuggestions.classList.add("active")
}

// Search and navigate function
function searchAndNavigate(query) {
  showLoading()

  // Simple parsing - you can enhance this
  const parts = query.split(",")
  const city = parts[0].trim()
  const country = parts[1] ? parts[1].trim() : ""

  navigateToLocation(city, country)
}

// Navigate to location - FIXED: Use correct route
function navigateToLocation(city, country) {
  showLoading()

  // Add to recent locations
  addToRecentLocations(city, country)

  // Navigate to weather detail page
  const url = country
    ? `/weather-detailed?city=${encodeURIComponent(city)}&country=${encodeURIComponent(country)}`
    : `/weather-detailed?city=${encodeURIComponent(city)}`

  console.log("Navigating to:", url)
  window.location.href = url
}

// Current location functionality
if (useCurrentLocation) {
  useCurrentLocation.addEventListener("click", () => {
    if (navigator.geolocation) {
      showLoading()
      navigator.geolocation.getCurrentPosition(
        (position) => {
          const lat = position.coords.latitude
          const lon = position.coords.longitude

          // Navigate using coordinates
          const url = `/weather-detailed?lat=${lat}&lon=${lon}`
          console.log("Navigating to:", url)
          window.location.href = url
        },
        (error) => {
          hideLoading()
          alert("Unable to get your location. Please search manually.")
          console.error("Geolocation error:", error)
        },
      )
    } else {
      alert("Geolocation is not supported by this browser.")
    }
  })
}

// Popular cities functionality
document.querySelectorAll(".city-btn").forEach((btn) => {
  btn.addEventListener("click", function () {
    const city = this.dataset.city
    const country = this.dataset.country
    navigateToLocation(city, country)
  })
})

// Recent locations management
function addToRecentLocations(city, country) {
  let recentLocations = JSON.parse(localStorage.getItem("recentLocations") || "[]")

  // Remove if already exists
  recentLocations = recentLocations.filter((loc) => !(loc.city === city && loc.country === country))

  // Add to beginning
  recentLocations.unshift({ city, country, timestamp: Date.now() })

  // Keep only last 5
  recentLocations = recentLocations.slice(0, 5)

  localStorage.setItem("recentLocations", JSON.stringify(recentLocations))
  displayRecentLocations()
}

function displayRecentLocations() {
  if (!recentLocationsList) return

  const recentLocations = JSON.parse(localStorage.getItem("recentLocations") || "[]")

  if (recentLocations.length === 0) {
    recentLocationsList.innerHTML = '<div class="recent-location-item">No recent locations</div>'
    return
  }

  recentLocationsList.innerHTML = ""
  recentLocations.forEach((location) => {
    const locationItem = document.createElement("button")
    locationItem.className = "recent-location-item"
    locationItem.innerHTML = `
            <i class="fas fa-history"></i>
            ${location.city}, ${location.country}
        `

    locationItem.addEventListener("click", () => {
      navigateToLocation(location.city, location.country)
    })

    recentLocationsList.appendChild(locationItem)
  })
}

// Loading functions
function showLoading() {
  const loadingOverlay = document.getElementById("loadingOverlay")
  if (loadingOverlay) {
    loadingOverlay.style.display = "flex"
  }
}

function hideLoading() {
  const loadingOverlay = document.getElementById("loadingOverlay")
  if (loadingOverlay) {
    loadingOverlay.style.display = "none"
  }
}

function refreshWeather() {
  showLoading()
  setTimeout(() => {
    location.reload()
  }, 1000)
}

// Initialize the page
document.addEventListener("DOMContentLoaded", () => {
  console.log("Weather Dashboard loaded successfully!")

  displayRecentLocations()
  updateTime()
  setInterval(updateTime, 60000) // Update time every minute

  // Close suggestions when clicking outside
  document.addEventListener("click", (e) => {
    if (!e.target.closest(".search-section")) {
      if (searchSuggestions) searchSuggestions.classList.remove("active")
    }
  })
})

function updateTime() {
  const now = new Date()
  const options = {
    weekday: "long",
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  }

  const timeElement = document.querySelector(".current-time")
  if (timeElement) {
    timeElement.textContent = now.toLocaleDateString("en-US", options)
  }
}
