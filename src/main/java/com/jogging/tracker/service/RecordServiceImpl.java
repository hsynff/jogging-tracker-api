package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.model.entity.Record;
import com.jogging.tracker.model.dto.RecordDto;
import com.jogging.tracker.model.dto.WeatherDto;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.model.entity.WeatherCondition;
import com.jogging.tracker.repository.RecordRepository;
import com.jogging.tracker.repository.UserRepository;
import com.jogging.tracker.util.CommonUtils;
import com.jogging.tracker.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;
    private final RSQLParser rsqlParser;
    private final WeatherService weatherService;

    @Override
    @Transactional
    public Record create(LocalDate date, Integer distance, Integer time, Double latitude, Double longitude, Long userId) throws AppException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_NOT_FOUND));

        Record record = new Record();
        record.setDate(date);
        record.setDistance(distance);
        record.setTime(time);
        record.setLatitude(latitude);
        record.setLongitude(longitude);
        record.setUser(user);

        WeatherDto weatherDto = weatherService.getWeather(latitude, longitude, date);

        WeatherCondition weatherCondition = WeatherDto.toEntity(weatherDto);

        weatherCondition.setRecord(record);
        record.setWeather(weatherCondition);

        return recordRepository.save(record);
    }

    @Override
    public DataFilterRespDto<RecordDto> getAll(String query, Integer page, Integer itemsPerPage, Long idUser) throws AppException {
        Page<Record> recordPage = null;

        try {
            if (query != null) {
                Specification<Record> recordSpec = rsqlParser.parse(query).accept(new CustomRsqlVisitor<>());

                if (idUser != null) {
                    recordSpec = recordSpec.and(RecordRepository.recordByUserSpec(idUser));
                }

                recordPage = recordRepository.findAll(recordSpec, PageRequest.of(page, itemsPerPage));
            } else {

                if (idUser != null) {
                    recordPage = recordRepository.findAll(RecordRepository.recordByUserSpec(idUser), PageRequest.of(page, itemsPerPage));

                } else {
                    recordPage = recordRepository.findAll(PageRequest.of(page, itemsPerPage));
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw CommonUtils.translateToAppException(e);
        }

        return new DataFilterRespDto<>(
                recordPage.getTotalElements(),
                recordPage.getTotalPages(),
                recordPage.get().map(r -> RecordDto.fromEntity(r, true, true))
                        .collect(Collectors.toList())
        );
    }

    @Override
    @Transactional
    public Record getById(Long id, Long userId) throws AppException {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.RECORD_NOT_FOUND));

        if (userId != null && !record.getUser().getId().equals(userId)) {
            throw new AppException(AppException.ErrorCodeMsg.RECORD_NOT_FOUND);
        }

        return record;
    }

    @Override
    @Transactional
    public Record update(Long id, LocalDate date, Integer distance, Integer time, Double latitude, Double longitude, Long userId) throws AppException {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.RECORD_NOT_FOUND));

        if (userId != null && !record.getUser().getId().equals(userId)) {
            throw new AppException(AppException.ErrorCodeMsg.RECORD_NOT_FOUND);
        }

        //update weather condition
        if (latitude != null || longitude != null) {

            //if date also changed or location is drastically different from previous
            if ((date != null && !date.isEqual(record.getDate())) || isLocationChangeExceeds(record.getLatitude(), record.getLongitude(), latitude, longitude, 0.03)) {
                WeatherDto weatherDto = weatherService.getWeather(
                        latitude != null ? latitude : record.getLatitude(),
                        longitude != null ? longitude : record.getLongitude(),
                        date != null ? date : record.getDate());

                WeatherCondition weatherCondition = record.getWeather();

                if (weatherCondition == null) {
                    weatherCondition = new WeatherCondition();
                    weatherCondition.setRecord(record);
                    record.setWeather(weatherCondition);
                }

                weatherCondition.setSummary(weatherDto.getSummary());
                weatherCondition.setTemperatureHigh(weatherDto.getTemperatureHigh());
                weatherCondition.setTemperatureLow(weatherDto.getTemperatureLow());
                weatherCondition.setWindSpeed(weatherDto.getWindSpeed());
                weatherCondition.setZone(weatherDto.getZone());

            }

        }

        if (date != null) {
            record.setDate(date);
        }

        if (distance != null) {
            record.setDistance(distance);
        }

        if (time != null) {
            record.setTime(time);
        }

        if (latitude != null) {
            record.setLatitude(latitude);
        }

        if (longitude != null) {
            record.setLongitude(longitude);
        }

        return recordRepository.save(record);
    }

    @Override
    @Transactional
    public Record delete(Long id, Long userId) throws AppException {
        Record record = recordRepository.findById(id).orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.RECORD_NOT_FOUND));

        if (userId != null && !record.getUser().getId().equals(userId)) {
            throw new AppException(AppException.ErrorCodeMsg.RECORD_NOT_FOUND);
        }

        recordRepository.delete(record);
        return record;
    }


    boolean isLocationChangeExceeds(Double oldLatitude, Double oldLongitude, Double newLatitude, Double newLongitude, Double change) {
        double latitudeChange = newLatitude != null ? Math.abs(newLatitude - oldLatitude) : 0;
        double longitudeChange = newLongitude != null ? Math.abs(newLongitude - oldLongitude) : 0;

        return latitudeChange > change || longitudeChange > change;
    }

}
