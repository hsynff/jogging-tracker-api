package com.jogging.tracker.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.WeatherDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherServiceImpl implements WeatherService {

    private final RestTemplate restTemplate;

    @Override
    public WeatherDto getWeather(Double latitude, Double longitude, LocalDate date) throws AppException {
        try {
            long epoch = LocalDateTime.of(date, LocalTime.NOON).toEpochSecond(ZoneOffset.UTC);
            String resp = restTemplate.getForObject(latitude + "," + longitude + "," + epoch, String.class);

            if (resp == null) {
                throw new AppException(AppException.ErrorCodeMsg.WEATHER_API_ERROR);
            }

            JsonNode jsonNode = new ObjectMapper().readTree(resp);
            JsonNode dailyNode = jsonNode.get("daily").get("data").get(0);

            WeatherDto dto = new WeatherDto();

            dto.setZone(jsonNode.get("timezone").textValue());
            dto.setSummary(dailyNode.get("summary").textValue());
            dto.setWindSpeed(dailyNode.get("windSpeed").doubleValue());
            dto.setTemperatureHigh(dailyNode.get("temperatureHigh").doubleValue());
            dto.setTemperatureLow(dailyNode.get("temperatureLow").doubleValue());

            return dto;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AppException.ErrorCodeMsg.WEATHER_API_ERROR, e.getMessage());
        }
    }

}
