package com.example.weather.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/business") // CHANGED FROM /weather-app to /business
public class BusinessController {

    @GetMapping("/")
    public String businessHome(Model model) {
        model.addAttribute("pageTitle", "WeatherPro Business - Professional Weather Services");
        return "home"; // Uses home.html (wrapped with layout.html)
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Weather Dashboard - WeatherPro Business");
        return "dashboard"; // Uses dashboard.html (wrapped with layout.html)
    }

    @GetMapping("/safety")
    public String safety(Model model) {
        model.addAttribute("pageTitle", "Safety Measures - WeatherPro Business");
        return "safety";
    }

    @GetMapping("/services/forecasting")
    public String forecasting(Model model) {
        model.addAttribute("pageTitle", "Weather Forecasting Services");
        return "services/forecasting";
    }

    @GetMapping("/services/alerts")
    public String alerts(Model model) {
        model.addAttribute("pageTitle", "Weather Alerts & Warnings");
        return "services/alerts";
    }

    @GetMapping("/services/analytics")
    public String analytics(Model model) {
        model.addAttribute("pageTitle", "Weather Analytics");
        return "services/analytics";
    }

    @GetMapping("/services/api")
    public String api(Model model) {
        model.addAttribute("pageTitle", "Weather API Services");
        return "services/api";
    }

    @GetMapping("/why-weatherpro")
    public String whyWeatherPro(Model model) {
        model.addAttribute("pageTitle", "Why Choose WeatherPro");
        return "why-weatherpro";
    }

    @GetMapping("/weather-events")
    public String weatherEvents(Model model) {
        model.addAttribute("pageTitle", "Weather Events");
        return "weather-events";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("pageTitle", "Contact Us");
        return "contact";
    }

    @GetMapping("/get-started")
    public String getStarted(Model model) {
        model.addAttribute("pageTitle", "Get Started");
        return "get-started";
    }

    @GetMapping("/support/documentation")
    public String documentation(Model model) {
        model.addAttribute("pageTitle", "Documentation");
        return "support/documentation";
    }

    @GetMapping("/support/faq")
    public String faq(Model model) {
        model.addAttribute("pageTitle", "Frequently Asked Questions");
        return "support/faq";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("pageTitle", "About WeatherPro");
        return "about";
    }
}
