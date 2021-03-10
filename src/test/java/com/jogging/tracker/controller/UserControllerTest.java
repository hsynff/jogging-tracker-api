package com.jogging.tracker.controller;

import com.jogging.tracker.model.dto.UserDto;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.service.UserService;
import com.jogging.tracker.testUtils.WebMvcUtils;
import com.jogging.tracker.testUtils.WithPrincipal;
import com.jogging.tracker.util.MailSenderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private MailSenderUtil mailSenderUtil;

    private static MockedStatic<UserDto> userDtoMockedStatic;

    @BeforeAll
    static void init() {
        userDtoMockedStatic = mockStatic(UserDto.class);
    }

    @AfterAll
    static void close() {
        userDtoMockedStatic.close();
    }

    @Test
    @WithPrincipal(role = User.Role.ROLE_ADMIN)
    void create() throws Exception {
        final String email = "em@ail";
        final String password = "321";
        final String firstName = "John";
        final String lastName = "Doe";

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setPassword(password);
        userDto.setRole(User.Role.ROLE_USER.toString());

        User mockUser = Mockito.mock(User.class);

        when(userService.create(anyString(), anyString(), anyString(), anyString(), any(User.Role.class), any(User.Status.class)))
                .thenReturn(mockUser);
        userDtoMockedStatic.when(() -> UserDto.fromEntity(mockUser)).thenReturn(userDto);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(WebMvcUtils.asJsonString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));
    }

    @Test
    void getAll() {
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