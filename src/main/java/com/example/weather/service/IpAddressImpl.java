package com.example.weather.service;

import org.springframework.stereotype.Service;

@Service
public class IpAddressImpl {

    public String getClientIpAddress(String xForwardedFor, String xRealIp, String remoteAddr) {

        // Replace deprecated StringUtils.isEmpty with null/blank checks
        if (xForwardedFor != null && !xForwardedFor.trim().isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        if (xRealIp != null && !xRealIp.trim().isEmpty()) {
            return xRealIp;
        }

        if (remoteAddr != null && !remoteAddr.trim().isEmpty()) {
            return remoteAddr;
        }

        return "127.0.0.1"; // Default fallback
    }

    public boolean isValidIpAddress(String ip) {
        if (ip == null || ip.trim().isEmpty()) {
            return false;
        }

        // Basic IP validation logic
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            return false;
        }

        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}