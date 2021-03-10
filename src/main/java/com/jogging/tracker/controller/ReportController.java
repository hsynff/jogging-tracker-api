package com.jogging.tracker.controller;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.ReportDto;
import com.jogging.tracker.service.ReportService;
import com.jogging.tracker.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/users/{userId}/reports/weekly")
    @PreAuthorize("((hasRole('ROLE_USER') or hasRole('ROLE_MANAGER')) and #userId == authentication.principal.id) or hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<ReportDto>> getWeeklyReport(@PathVariable("userId") Long userId, @RequestParam("date") String date) throws AppException {
        List<ReportDto> weeklyReport = reportService.getWeeklyReport(CommonUtils.fromYearMonthString(date), userId);
        return ResponseEntity.ok(weeklyReport);
    }
}
