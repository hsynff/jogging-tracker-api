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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserRecordsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RecordService recordService;
    @MockBean
    private MailSenderUtil mailSenderUtil;

    @Test
    @WithPrincipal(role = User.Role.ROLE_USER, id = 1L)
    void getAll() throws Exception {
        DataFilterReqDto reqDto = new DataFilterReqDto();
        reqDto.setPage(0);
        reqDto.setItemsPerPage(10);
        reqDto.setQuery("distance eq 1");

        DataFilterRespDto<RecordDto> respDto = Mockito.mock(DataFilterRespDto.class);

        when(recordService.getAll(reqDto.getQuery(), reqDto.getPage(), reqDto.getItemsPerPage(), 1L)).thenReturn(respDto);

        mockMvc.perform(get("/users/1/records")
                .queryParam("page", reqDto.getPage().toString())
                .queryParam("itemsPerPage", reqDto.getItemsPerPage().toString())
                .queryParam("query", reqDto.getQuery())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }


    @Test
    void create() {

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