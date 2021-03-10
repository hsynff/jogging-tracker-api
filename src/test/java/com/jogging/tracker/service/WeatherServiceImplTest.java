package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.WeatherDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceImplTest {

    @InjectMocks
    private WeatherServiceImpl weatherService;

    @Mock
    private RestTemplate restTemplate;

    private AutoCloseable autoCloseable;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void getWeather() throws AppException {
        when(restTemplate.getForObject(anyString(), any())).thenReturn(getDummyResponseString());

        WeatherDto weather = weatherService.getWeather(1.0, 1.0, LocalDate.now());

        assertNotNull(weather);
        assertNotNull(weather.getSummary());
        assertNotNull(weather.getTemperatureHigh());
        assertNotNull(weather.getTemperatureLow());
        assertNotNull(weather.getWindSpeed());
        assertNotNull(weather.getZone());

    }


    private String getDummyResponseString(){
        return "{\n" +
                "   \"latitude\":37.774929,\n" +
                "   \"longitude\":-122.419418,\n" +
                "   \"timezone\":\"America/Los_Angeles\",\n" +
                "   \"currently\":{\n" +
                "      \"time\":1615028928,\n" +
                "      \"summary\":\"Mostly Cloudy\",\n" +
                "      \"icon\":\"partly-cloudy-night\",\n" +
                "      \"precipIntensity\":0,\n" +
                "      \"precipProbability\":0,\n" +
                "      \"temperature\":47.05,\n" +
                "      \"apparentTemperature\":43.21,\n" +
                "      \"dewPoint\":42.89,\n" +
                "      \"humidity\":0.85,\n" +
                "      \"pressure\":1019.1,\n" +
                "      \"windSpeed\":7.9,\n" +
                "      \"windGust\":12.36,\n" +
                "      \"windBearing\":254,\n" +
                "      \"cloudCover\":0.87,\n" +
                "      \"uvIndex\":0,\n" +
                "      \"visibility\":10,\n" +
                "      \"ozone\":378.6\n" +
                "   },\n" +
                "   \"minutely\":{},\n" +
                "   \"hourly\":{},\n" +
                "   \"daily\":{\n" +
                "      \"data\":[\n" +
                "         {\n" +
                "            \"time\":1615017600,\n" +
                "            \"summary\":\"Partly cloudy throughout the day.\",\n" +
                "            \"icon\":\"rain\",\n" +
                "            \"sunriseTime\":1615041300,\n" +
                "            \"sunsetTime\":1615083000,\n" +
                "            \"moonPhase\":0.79,\n" +
                "            \"precipIntensity\":0.0051,\n" +
                "            \"precipIntensityMax\":0.1161,\n" +
                "            \"precipIntensityMaxTime\":1615017600,\n" +
                "            \"precipProbability\":0.98,\n" +
                "            \"precipType\":\"rain\",\n" +
                "            \"temperatureHigh\":53.36,\n" +
                "            \"temperatureHighTime\":1615066260,\n" +
                "            \"temperatureLow\":44.5,\n" +
                "            \"temperatureLowTime\":1615126500,\n" +
                "            \"apparentTemperatureHigh\":52.86,\n" +
                "            \"apparentTemperatureHighTime\":1615066260,\n" +
                "            \"apparentTemperatureLow\":42.24,\n" +
                "            \"apparentTemperatureLowTime\":1615126980,\n" +
                "            \"dewPoint\":41.65,\n" +
                "            \"humidity\":0.77,\n" +
                "            \"pressure\":1020.5,\n" +
                "            \"windSpeed\":9.27,\n" +
                "            \"windGust\":17.28,\n" +
                "            \"windGustTime\":1615017600,\n" +
                "            \"windBearing\":286,\n" +
                "            \"cloudCover\":0.51,\n" +
                "            \"uvIndex\":4,\n" +
                "            \"uvIndexTime\":1615063980,\n" +
                "            \"visibility\":10,\n" +
                "            \"ozone\":354.5,\n" +
                "            \"temperatureMin\":45.01,\n" +
                "            \"temperatureMinTime\":1615042680,\n" +
                "            \"temperatureMax\":53.36,\n" +
                "            \"temperatureMaxTime\":1615066260,\n" +
                "            \"apparentTemperatureMin\":41.64,\n" +
                "            \"apparentTemperatureMinTime\":1615042740,\n" +
                "            \"apparentTemperatureMax\":52.86,\n" +
                "            \"apparentTemperatureMaxTime\":1615066260\n" +
                "         }\n" +
                "      ]\n" +
                "   },\n" +
                "   \"flags\":{\n" +
                "      \"sources\":[\n" +
                "         \"cmc\",\n" +
                "         \"gfs\",\n" +
                "         \"hrrr\",\n" +
                "         \"icon\",\n" +
                "         \"isd\",\n" +
                "         \"madis\",\n" +
                "         \"nam\",\n" +
                "         \"sref\",\n" +
                "         \"darksky\"\n" +
                "      ],\n" +
                "      \"nearest-station\":0.425,\n" +
                "      \"units\":\"us\"\n" +
                "   },\n" +
                "   \"offset\":-8\n" +
                "}";
    }

}