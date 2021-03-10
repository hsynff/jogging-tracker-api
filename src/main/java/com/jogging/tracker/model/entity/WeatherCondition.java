package com.jogging.tracker.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "weather_condition")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "zone", nullable = false)
    private String zone;

    @Column(name = "summary", nullable = false)
    @Lob
    private String summary;

    @Column(name = "temperature_high", nullable = false)
    private Double temperatureHigh;

    @Column(name = "temperature_low", nullable = false)
    private Double temperatureLow;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @OneToOne(mappedBy = "weather")
    private Record record;
}
