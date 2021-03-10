package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.ReportDto;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.repository.RecordRepository;
import com.jogging.tracker.repository.UserRepository;
import lombok.Getter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.jogging.tracker.config.AppException.ErrorCodeMsg.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceImplTest {

    @InjectMocks
    @Spy
    private ReportServiceImpl reportService;

    @Mock
    private RecordRepository recordRepository;
    @Mock
    private UserRepository userRepository;

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
    void getWeeklyReport_whenIncorrectUserID_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> reportService.getWeeklyReport(YearMonth.now(), 1L));

        assertEquals(USER_NOT_FOUND.getMessage(), ex.getMessage());
    }


    @Test
    void getWeeklyReport() throws AppException {
        User dummyUser = new User();
        dummyUser.setId(1L);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(dummyUser));

        RecordRepository.ReportTuple reportTuple1 = new ReportTupleImpl(LocalDate.now(), LocalDate.now(), 1, 1.0, 1.0);
        RecordRepository.ReportTuple reportTuple2 = new ReportTupleImpl(LocalDate.now(), LocalDate.now(), 2, 2.0, 2.0);

        when(recordRepository.generateWeeklyReport(anyInt(), anyInt(), anyLong())).thenReturn(Arrays.asList(reportTuple1, reportTuple2));

        List<ReportDto> weeklyReport = reportService.getWeeklyReport(YearMonth.now(), 1L);

        assertFalse(weeklyReport.isEmpty());
        assertEquals(2, weeklyReport.size());

    }


    @Getter
    class ReportTupleImpl implements RecordRepository.ReportTuple {

        private LocalDate dateFrom;
        private LocalDate dateTo;
        private Integer weekOfYear;
        private Double avgSpeed;
        private Double avgDistance;

        ReportTupleImpl(LocalDate dateFrom, LocalDate dateTo, Integer weekOfYear, Double avgSpeed, Double avgDistance) {
            this.dateFrom = dateFrom;
            this.dateTo = dateTo;
            this.weekOfYear = weekOfYear;
            this.avgSpeed = avgSpeed;
            this.avgDistance = avgDistance;
        }


    }

}