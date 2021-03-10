package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.model.entity.Record;
import com.jogging.tracker.model.dto.RecordDto;

import java.time.LocalDate;

public interface RecordService {
    Record create(LocalDate date, Integer distance, Integer time, Double latitude, Double longitude, Long userId) throws AppException;

    DataFilterRespDto<RecordDto> getAll(String query, Integer page, Integer itemsPerPage, Long userId) throws AppException;

    Record getById(Long id, Long userId) throws AppException;

    Record update(Long id, LocalDate date, Integer distance, Integer time, Double latitude, Double longitude, Long userId) throws AppException;

    Record delete(Long id, Long userId) throws AppException;

}
