package com.example.weather.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "location")
public class LocationConfigProperties {

    private String url;
    private String key;
    private String format;

    private Api api = new Api();

    // Getters and Setters
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public Api getApi() { return api; }
    public void setApi(Api api) { this.api = api; }

    public static class Api {
        private String url;
        private String key;
        private String format;

        // Getters and Setters
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getKey() { return key; }
        public void setKey(String key) { this.key = key; }

        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
    }
}