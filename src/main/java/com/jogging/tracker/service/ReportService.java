package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.ReportDto;

import java.time.YearMonth;
import java.util.List;

public interface ReportService {
    List<ReportDto> getWeeklyReport(YearMonth yearMonth, Long idUser) throws AppException;
}
