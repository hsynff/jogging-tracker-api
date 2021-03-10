package com.jogging.tracker.controller;

import com.jogging.tracker.model.dto.InviteDto;
import com.jogging.tracker.config.jwt.JwtTokenProvider;
import com.jogging.tracker.model.dto.UserDto;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.service.UserService;
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
import org.springframework.test.web.servlet.MvcResult;

import static com.jogging.tracker.testUtils.WebMvcUtils.asJsonString;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
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
    void login() throws Exception {

        final String token = "DUMMY_TOKEN";

        User userMock = Mockito.mock(User.class);
        when(userService.login(anyString(), anyString())).thenReturn(userMock);
        when(jwtTokenProvider.generateJwtToken(userMock)).thenReturn(token);

        UserDto requestDto = new UserDto();
        requestDto.setEmail("em@ail");
        requestDto.setPassword("123");

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(token, mvcResult.getResponse().getContentAsString());
    }

    @Test
    void register() throws Exception {
        final String email = "em@ail";
        final String password = "321";
        final String firstName = "John";
        final String lastName = "Doe";

        final String dummyToken = "DUMMY_TOKEN";

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setPassword(password);

        User mockUser = Mockito.mock(User.class);

        when(userService.create(anyString(), anyString(), anyString(), anyString(), any(User.Role.class), any(User.Status.class)))
                .thenReturn(mockUser);
        when(jwtTokenProvider.generateJwtToken(mockUser)).thenReturn(dummyToken);
        when(mockUser.getEmail()).thenReturn(email);
        doNothing().when(mailSenderUtil).sendVerifyMail(email, dummyToken);

        userDtoMockedStatic.when(() -> UserDto.fromEntity(mockUser)).thenReturn(userDto);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));

        verify(jwtTokenProvider, times(1)).generateJwtToken(mockUser);
        verify(mailSenderUtil, times(1)).sendVerifyMail(email, dummyToken);

    }

    @Test
    void registerFromInvite() throws Exception {
        final String email = "em@ail";
        final String password = "321";
        final String firstName = "John";
        final String lastName = "Doe";

        final String inviteToken = "DUMMY_TOKEN";

        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setPassword(password);

        User mockUser = Mockito.mock(User.class);

        when(jwtTokenProvider.validateToken(inviteToken)).thenReturn(true);
        when(jwtTokenProvider.getMailFromInviteToken(inviteToken)).thenReturn(email);
        when(userService.create(anyString(), anyString(), anyString(), anyString(), any(User.Role.class), any(User.Status.class)))
                .thenReturn(mockUser);

        userDtoMockedStatic.when(() -> UserDto.fromEntity(mockUser)).thenReturn(userDto);

        mockMvc.perform(post("/auth/register")
                .queryParam("inviteToken", inviteToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .accept(MediaType.APPLICATION_JSON)
                .param("email", email)
                .param("firstName", firstName)
                .param("lastName", lastName)
                .param("password", password)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName));

        verify(jwtTokenProvider, never()).generateJwtToken(mockUser);
        verify(mailSenderUtil, never()).sendVerifyMail(email, "");
    }


    @Test
    void activate() throws Exception {
        User mockUser = Mockito.mock(User.class);
        UserDto mockUserDto = Mockito.mock(UserDto.class);

        final String token = "DUMMY_TOKEN";

        when(jwtTokenProvider.validateToken(token)).thenReturn(true);
        when(jwtTokenProvider.createUserFromJwt(token)).thenReturn(mockUser);
        when(mockUser.getId()).thenReturn(1L);
        when(userService.verify(1L)).thenReturn(mockUser);

        userDtoMockedStatic.when(() -> UserDto.fromEntity(mockUser)).thenReturn(mockUserDto);

        mockMvc.perform(get("/auth/verify")
                .queryParam("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    @WithPrincipal(role = User.Role.ROLE_ADMIN, id = 1)
    void invite() throws Exception {
        final String email = "em@il";
        final String token = "DUMMY_TOKEN";

        InviteDto inviteDto = new InviteDto(email);

        when(userService.existsByEmail(email)).thenReturn(false);
        when(jwtTokenProvider.generateInviteToken(email)).thenReturn(token);
        doNothing().when(mailSenderUtil).sendInviteMail(email, token);

        mockMvc.perform(post("/auth/invite")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(asJsonString(inviteDto)))
                .andDo(print())
                .andExpect(status().isAccepted());

    }

}