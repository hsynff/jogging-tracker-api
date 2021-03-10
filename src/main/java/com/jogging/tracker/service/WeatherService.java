package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.WeatherDto;

import java.time.LocalDate;

public interface WeatherService {
    WeatherDto getWeather(Double latitude, Double longitude, LocalDate date) throws AppException;
}
