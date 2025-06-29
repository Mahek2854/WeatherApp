// Professional Business Website JavaScript

document.addEventListener("DOMContentLoaded", () => {
  // Search Toggle
  const searchToggle = document.getElementById("searchToggle")
  const searchBar = document.getElementById("searchBar")

  if (searchToggle && searchBar) {
    searchToggle.addEventListener("click", () => {
      searchBar.classList.toggle("active")
      if (searchBar.classList.contains("active")) {
        searchBar.querySelector(".search-input").focus()
      }
    })
  }

  // Mobile Menu Toggle
  const mobileMenuToggle = document.getElementById("mobileMenuToggle")
  const navMenu = document.querySelector(".nav-menu")

  if (mobileMenuToggle && navMenu) {
    mobileMenuToggle.addEventListener("click", () => {
      navMenu.classList.toggle("active")
    })
  }

  // Smooth Scrolling for Anchor Links
  document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
    anchor.addEventListener("click", function (e) {
      e.preventDefault()
      const target = document.querySelector(this.getAttribute("href"))
      if (target) {
        target.scrollIntoView({
          behavior: "smooth",
          block: "start",
        })
      }
    })
  })

  // Navbar Background on Scroll
  const navbar = document.querySelector(".main-header")
  if (navbar) {
    window.addEventListener("scroll", () => {
      if (window.scrollY > 100) {
        navbar.classList.add("scrolled")
      } else {
        navbar.classList.remove("scrolled")
      }
    })
  }

  // Service Cards Animation on Scroll
  const observerOptions = {
    threshold: 0.1,
    rootMargin: "0px 0px -50px 0px",
  }

  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        entry.target.style.opacity = "1"
        entry.target.style.transform = "translateY(0)"
      }
    })
  }, observerOptions)

  // Observe service cards and industry cards
  document.querySelectorAll(".service-card, .industry-card").forEach((card) => {
    card.style.opacity = "0"
    card.style.transform = "translateY(30px)"
    card.style.transition = "opacity 0.6s ease, transform 0.6s ease"
    observer.observe(card)
  })

  // Search Functionality
  const searchInput = document.querySelector(".search-input")
  if (searchInput) {
    searchInput.addEventListener("keypress", function (e) {
      if (e.key === "Enter") {
        performSearch(this.value)
      }
    })
  }

  const searchBtn = document.querySelector(".search-btn")
  if (searchBtn) {
    searchBtn.addEventListener("click", () => {
      const searchInput = document.querySelector(".search-input")
      if (searchInput) {
        performSearch(searchInput.value)
      }
    })
  }

  function performSearch(query) {
    if (query.trim()) {
      // Redirect to search results or weather dashboard
      window.location.href = `/weather-app/dashboard?search=${encodeURIComponent(query)}`
    }
  }

  // Close dropdowns when clicking outside
  document.addEventListener("click", (e) => {
    if (!e.target.closest(".nav-item")) {
      document.querySelectorAll(".dropdown-menu").forEach((menu) => {
        menu.style.opacity = "0"
        menu.style.visibility = "hidden"
        menu.style.transform = "translateY(-10px)"
      })
    }
  })

  // Add loading states for buttons
  document.querySelectorAll(".btn").forEach((btn) => {
    btn.addEventListener("click", function (e) {
      if (this.href && !this.href.includes("#")) {
        this.style.opacity = "0.7"
        this.style.pointerEvents = "none"

        setTimeout(() => {
          this.style.opacity = "1"
          this.style.pointerEvents = "auto"
        }, 2000)
      }
    })
  })

  console.log("WeatherPro Business website loaded successfully!")
})

// Utility Functions
function showNotification(message, type = "info") {
  const notification = document.createElement("div")
  notification.className = `notification notification-${type}`
  notification.textContent = message

  notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        background: ${type === "success" ? "#27ae60" : type === "error" ? "#e74c3c" : "#3498db"};
        color: white;
        padding: 15px 20px;
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.2);
        z-index: 10000;
        opacity: 0;
        transform: translateX(100%);
        transition: all 0.3s ease;
    `

  document.body.appendChild(notification)

  setTimeout(() => {
    notification.style.opacity = "1"
    notification.style.transform = "translateX(0)"
  }, 100)

  setTimeout(() => {
    notification.style.opacity = "0"
    notification.style.transform = "translateX(100%)"
    setTimeout(() => {
      document.body.removeChild(notification)
    }, 300)
  }, 3000)
}

// Weather Data Refresh Function
function refreshWeatherData() {
  showNotification("Refreshing weather data...", "info")

  setTimeout(() => {
    location.reload()
  }, 1000)
}
