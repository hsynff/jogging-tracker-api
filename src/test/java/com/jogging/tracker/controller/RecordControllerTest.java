package com.jogging.tracker.controller;

import com.jogging.tracker.model.dto.DataFilterReqDto;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.model.dto.RecordDto;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.service.RecordService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordService recordService;
    @MockBean
    private MailSenderUtil mailSenderUtil;

    @Test
    @WithPrincipal(role = User.Role.ROLE_ADMIN)
    void getAll() throws Exception {
        DataFilterReqDto reqDto = new DataFilterReqDto();
        reqDto.setPage(0);
        reqDto.setItemsPerPage(10);
        reqDto.setQuery("distance eq 1");

        DataFilterRespDto<RecordDto> respDto = Mockito.mock(DataFilterRespDto.class);

        when(recordService.getAll(reqDto.getQuery(), reqDto.getPage(), reqDto.getItemsPerPage(), null)).thenReturn(respDto);

        mockMvc.perform(get("/records")
                .queryParam("page", reqDto.getPage().toString())
                .queryParam("itemsPerPage", reqDto.getItemsPerPage().toString())
                .queryParam("query", reqDto.getQuery())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void getOne() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

}