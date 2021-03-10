package com.jogging.tracker.controller;

import com.jogging.tracker.model.dto.ReportDto;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.service.ReportService;
import com.jogging.tracker.testUtils.WithPrincipal;
import com.jogging.tracker.util.MailSenderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.YearMonth;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;
    @MockBean
    private MailSenderUtil mailSenderUtil;


    @Test
    @WithPrincipal(role = User.Role.ROLE_USER, id = 1L)
    void getWeeklyReport() throws Exception {
        ReportDto reportDtoMock = Mockito.mock(ReportDto.class);

        when(reportService.getWeeklyReport(any(YearMonth.class), anyLong()))
                .thenReturn(Collections.singletonList(reportDtoMock));

        mockMvc.perform(get("/users/1/reports/weekly")
                .queryParam("date", "2021-02")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}