package com.jogging.tracker.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jogging.tracker.model.entity.Record;
import com.jogging.tracker.util.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import static com.jogging.tracker.util.Markers.CreateRecord;
import static com.jogging.tracker.util.Markers.UpdateRecord;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecordDto {
    private Long id;

    @NotEmpty(groups = {CreateRecord.class})
    private String date;

    @NotNull(groups = {CreateRecord.class})
    @Min(value = 1, groups = {CreateRecord.class, UpdateRecord.class})
    private Integer distance;

    @NotNull(groups = {CreateRecord.class})
    @Min(value = 1, groups = {CreateRecord.class, UpdateRecord.class})
    private Integer time;

    @NotNull(groups = {CreateRecord.class})
    @Min(value = -90, groups = {CreateRecord.class, UpdateRecord.class})
    @Max(value = 90, groups = {CreateRecord.class, UpdateRecord.class})
    private Double latitude;

    @NotNull(groups = {CreateRecord.class})
    @Min(value = -180, groups = {CreateRecord.class, UpdateRecord.class})
    @Max(value = 180, groups = {CreateRecord.class, UpdateRecord.class})
    private Double longitude;

    private UserDto user;

    private WeatherDto weather;

    public static RecordDto fromEntity(Record record, boolean withUserDetails, boolean withWeatherDetails) {
        RecordDto dto = new RecordDto();
        dto.setId(record.getId());
        dto.setDate(CommonUtils.toDateString(record.getDate()));
        dto.setDistance(record.getDistance());
        dto.setTime(record.getTime());
        dto.setLatitude(record.getLatitude());
        dto.setLongitude(record.getLongitude());

        if (withUserDetails) {
            dto.setUser(UserDto.fromEntity(record.getUser()));
        }

        if (withWeatherDetails && record.getWeather() != null) {
            dto.setWeather(WeatherDto.fromEntity(record.getWeather()));
        }

        return dto;
    }

}
