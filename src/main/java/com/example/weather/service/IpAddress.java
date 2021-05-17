package com.example.weather.service;

import javax.servlet.http.HttpServletRequest;

public interface IpAddress {
    String getClientIp(HttpServletRequest request);
}
