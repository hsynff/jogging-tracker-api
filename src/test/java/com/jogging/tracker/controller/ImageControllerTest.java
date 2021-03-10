package com.jogging.tracker.controller;

import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.service.UserService;
import com.jogging.tracker.testUtils.WithPrincipal;
import com.jogging.tracker.util.MailSenderUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private MailSenderUtil mailSenderUtil;


    @Test
    @WithPrincipal(role = User.Role.ROLE_USER, id = 1)
    void uploadImage() throws Exception {

        Resource resource = new ClassPathResource("images/testImage.jpg");

        when(userService.addOrUpdateImage(anyLong(), any(), anyString())).thenReturn(null);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/users/1/images");
        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        mockMvc.perform(builder
                .file(new MockMultipartFile("image", resource.getFilename(), "image/jpeg", resource.getInputStream()))
        )
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithPrincipal(role = User.Role.ROLE_USER, id = 1)
    void downloadImage() throws Exception {
        when(userService.downloadImage(anyLong())).thenReturn(new byte[0]);

        mockMvc.perform(get("/users/1/images")
                .accept(MediaType.IMAGE_JPEG_VALUE))
                .andDo(print())
                .andExpect(status().isOk());
    }

}