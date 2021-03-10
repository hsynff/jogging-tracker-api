package com.jogging.tracker.repository;

import com.jogging.tracker.util.CommonUtils;
import com.jogging.tracker.model.dto.ReportDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql("classpath:sql/insert_user_active.sql")
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
class RecordRepositoryTest {

    @Autowired
    private RecordRepository recordRepository;

    @Test
    @Sql("classpath:sql/insert_records_five.sql")
    @Sql("classpath:sql/insert_records_five_2.sql")
    @Sql("classpath:sql/insert_records_five_3.sql")
    void generateWeeklyReport() {
        List<RecordRepository.ReportTuple> reportTuples = recordRepository.generateWeeklyReport(2021, 2, 1L);

        assertEquals(4, reportTuples.size());

        List<ReportDto> dtoList = reportTuples.stream()
                .map(t -> new ReportDto(
                        CommonUtils.toDateString(t.getDateFrom()),
                        CommonUtils.toDateString(t.getDateTo()),
                        t.getWeekOfYear(),
                        t.getAvgSpeed(),
                        t.getAvgDistance()))
                .collect(Collectors.toList());

        assertEquals(6, dtoList.get(0).getWeekOfYear());
        assertEquals(36, dtoList.get(0).getAvgDistance());

        assertEquals(7, dtoList.get(1).getWeekOfYear());
        assertEquals(45, dtoList.get(1).getAvgDistance());

        assertEquals(9, dtoList.get(2).getWeekOfYear());
        assertEquals(35, dtoList.get(2).getAvgDistance());

        assertEquals(10, dtoList.get(3).getWeekOfYear());
        assertEquals(60, dtoList.get(3).getAvgDistance());


    }
}