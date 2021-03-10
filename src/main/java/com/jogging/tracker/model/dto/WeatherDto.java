package com.jogging.tracker.model.dto;

import com.jogging.tracker.model.entity.WeatherCondition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDto {
    private Long id;
    private String zone;
    private String summary;
    private Double temperatureHigh;
    private Double temperatureLow;
    private Double windSpeed;

    public static WeatherDto fromEntity(WeatherCondition weatherCondition) {
        WeatherDto dto = new WeatherDto();
        dto.setId(weatherCondition.getId());
        dto.setZone(weatherCondition.getZone());
        dto.setSummary(weatherCondition.getSummary());
        dto.setTemperatureHigh(weatherCondition.getTemperatureHigh());
        dto.setTemperatureLow(weatherCondition.getTemperatureLow());
        dto.setWindSpeed(weatherCondition.getWindSpeed());

        return dto;
    }

    public static WeatherCondition toEntity(WeatherDto dto) {
        WeatherCondition weatherCondition = new WeatherCondition();

        weatherCondition.setZone(dto.getZone());
        weatherCondition.setSummary(dto.getSummary());
        weatherCondition.setTemperatureHigh(dto.getTemperatureHigh());
        weatherCondition.setTemperatureLow(dto.getTemperatureLow());
        weatherCondition.setWindSpeed(dto.getWindSpeed());

        return weatherCondition;
    }
}
