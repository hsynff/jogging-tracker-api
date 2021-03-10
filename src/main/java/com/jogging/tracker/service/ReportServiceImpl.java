package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.repository.RecordRepository;
import com.jogging.tracker.repository.UserRepository;
import com.jogging.tracker.util.CommonUtils;
import com.jogging.tracker.model.dto.ReportDto;
import com.jogging.tracker.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final RecordRepository recordRepository;
    private final UserRepository userRepository;

    @Override
    public List<ReportDto> getWeeklyReport(YearMonth yearMonth, Long idUser) throws AppException {
        User user = userRepository.findById(idUser)
                .orElseThrow(() -> new AppException(AppException.ErrorCodeMsg.USER_NOT_FOUND));

        List<RecordRepository.ReportTuple> reportTuples = recordRepository.generateWeeklyReport(
                yearMonth.getYear(),
                yearMonth.getMonthValue(),
                user.getId()
        );

        return reportTuples.stream()
                .map(t -> new ReportDto(CommonUtils.toDateString(t.getDateFrom()), CommonUtils.toDateString(t.getDateTo()), t.getWeekOfYear(), t.getAvgSpeed(), t.getAvgDistance()))
                .collect(Collectors.toList());
    }
}
