package com.jogging.tracker.service;

import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.model.dto.UserDto;
import com.jogging.tracker.model.entity.ImageData;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.repository.UserRepository;
import com.jogging.tracker.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.Mockito.*;


class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private RSQLParser rsqlParser;
    @Mock
    private PasswordEncoder passwordEncoder;

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
    void create() throws AppException {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).then(returnsFirstArg());
        when(passwordEncoder.encode(anyString())).thenReturn("321");

        final String email = "em@ail";
        final String password = "321";
        final String firstName = "John";
        final String lastName = "Doe";
        final User.Role role = User.Role.ROLE_USER;
        final User.Status status = User.Status.NEW;

        User user = userService.create(email, firstName, lastName, password, role, status);

        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(role, user.getRole());
        assertEquals(status, user.getStatus());

    }

    @Test
    void create_whenUserEmailExists_thenThrowException() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        final String email = "em@ail";
        final String password = "321";
        final String firstName = "John";
        final String lastName = "Doe";
        final User.Role role = User.Role.ROLE_USER;
        final User.Status status = User.Status.NEW;

        AppException ex = assertThrows(AppException.class, () -> userService.create(email, firstName, lastName, password, role, status));

        Assertions.assertEquals(AppException.ErrorCodeMsg.USER_ALREADY_EXISTS.getMessage(), ex.getMessage());
    }

    @Test
    void getAll() throws AppException {
        User dummyUser1 = new User();
        dummyUser1.setRole(User.Role.ROLE_USER);
        dummyUser1.setStatus(User.Status.NEW);

        User dummyUser2 = new User();
        dummyUser2.setRole(User.Role.ROLE_USER);
        dummyUser2.setStatus(User.Status.NEW);


        Node nodeMock = Mockito.mock(Node.class);
        Specification<User> specificationMock = Mockito.mock(Specification.class);

        when(rsqlParser.parse(anyString())).thenReturn(nodeMock);
        when(nodeMock.accept(any(CustomRsqlVisitor.class))).thenReturn(specificationMock);
        when(userRepository.findAll(any(Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl(Arrays.asList(dummyUser1, dummyUser2)));

        DataFilterRespDto<UserDto> respDto = userService.getAll("name eq David", 0, 25);

        assertEquals(2, respDto.getTotalElements());
        assertEquals(1, respDto.getTotalPages());
        assertEquals(2, respDto.getData().size());


    }

    @Test
    void getById() throws AppException {
        User userMock = Mockito.mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userMock));

        User user = userService.getById(1L);

        assertEquals(userMock, user);
    }

    @Test
    void getById_whenIncorrectUserId_thenThrowException() throws AppException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.getById(1L));

        assertEquals(AppException.ErrorCodeMsg.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void update_whenIncorrectUserId_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> userService.update(1L, null, null, null, null, null, null));

        assertEquals(AppException.ErrorCodeMsg.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void update() throws AppException {
        User dummyUser = new User();
        dummyUser.setFailedLoginAttempts(3);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.encode(anyString())).thenReturn("321");

        final String email = "em@ail";
        final String password = "321";
        final String firstName = "John";
        final String lastName = "Doe";
        final User.Role role = User.Role.ROLE_USER;
        final User.Status status = User.Status.ACTIVE;

        User user = userService.update(1L, email, firstName, lastName, password, role, status);

        assertEquals(0, user.getFailedLoginAttempts());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(role, user.getRole());
        assertEquals(status, user.getStatus());

    }

    @Test
    void verify_whenIncorrectUserId_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> userService.verify(1L));

        assertEquals(AppException.ErrorCodeMsg.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void verify_whenUserStatusActive_thenThrowException() {
        User userMock = Mockito.mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userMock));
        when(userMock.getStatus()).thenReturn(User.Status.ACTIVE);

        AppException ex = assertThrows(AppException.class,
                () -> userService.verify(1L));

        assertEquals(AppException.ErrorCodeMsg.USER_ALREADY_VERIFIED.getMessage(), ex.getMessage());
    }

    @Test
    void verify() throws AppException {
        User userDummy = new User();
        userDummy.setStatus(User.Status.NEW);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userDummy));

        User user = userService.verify(1L);

        assertEquals(User.Status.ACTIVE, user.getStatus());
    }

    @Test
    void delete_whenIncorrectUserId_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> userService.delete(1L));

        assertEquals(AppException.ErrorCodeMsg.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void delete() throws AppException {
        User userMock = Mockito.mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userMock));
        doNothing().when(userRepository).delete(any(User.class));

        User user = userService.delete(1L);

        assertEquals(userMock, user);

    }

    @Test
    void login_whenIncorrectEmail_thenThrowException() {
        when(userRepository.findFirstByEmail(anyString())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.login("em@ail", "123"));

        Assertions.assertEquals(AppException.ErrorCodeMsg.USER_CREDENTIALS_NOT_VALID.getMessage(), ex.getMessage());
    }

    @Test
    void login_whenNewUser_thenThrowException() {
        User userMock = Mockito.mock(User.class);
        when(userRepository.findFirstByEmail(anyString())).thenReturn(Optional.of(userMock));
        when(userMock.getStatus()).thenReturn(User.Status.NEW);

        AppException ex = assertThrows(AppException.class, () -> userService.login("em@ail", "123"));

        Assertions.assertEquals(AppException.ErrorCodeMsg.USER_NOT_VERIFIED.getMessage(), ex.getMessage());
    }

    @Test
    void login_whenBlockedUser_thenThrowException() {
        User userMock = Mockito.mock(User.class);
        when(userRepository.findFirstByEmail(anyString())).thenReturn(Optional.of(userMock));
        when(userMock.getStatus()).thenReturn(User.Status.BLOCKED);

        AppException ex = assertThrows(AppException.class, () -> userService.login("em@ail", "123"));

        Assertions.assertEquals(AppException.ErrorCodeMsg.USER_BLOCKED.getMessage(), ex.getMessage());
    }

    @Test
    void login_whenIncorrectPassword_thenThrowException() {
        User userMock = Mockito.mock(User.class);
        when(userRepository.findFirstByEmail(anyString())).thenReturn(Optional.of(userMock));
        when(userMock.getStatus()).thenReturn(User.Status.ACTIVE);
        when(userMock.getPassword()).thenReturn("123");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> userService.login("em@ail", "123"));

        Assertions.assertEquals(AppException.ErrorCodeMsg.USER_CREDENTIALS_NOT_VALID.getMessage(), ex.getMessage());
    }

    @Test
    void login() throws AppException {
        User dummyUser = new User();
        dummyUser.setStatus(User.Status.ACTIVE);
        dummyUser.setPassword("123");
        dummyUser.setFailedLoginAttempts(2);

        when(userRepository.findFirstByEmail(anyString())).thenReturn(Optional.of(dummyUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        User user = userService.login("em@ail", "123");

        assertNotNull(user);
        assertEquals(0, user.getFailedLoginAttempts());

    }


    @Test
    void existsByEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        boolean existsByEmail = userService.existsByEmail("em@ail");

        assertTrue(existsByEmail);
    }

    @Test
    void addOrUpdateImage_whenIncorrectUserId_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> userService.addOrUpdateImage(1L, null, null));

        assertEquals(AppException.ErrorCodeMsg.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void addOrUpdateImage_addImage() throws AppException, IOException {
        Resource resource = new ClassPathResource("images/testImage.jpg");
        User user = new User();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        ImageData imageData = userService.addOrUpdateImage(
                1L,
                FileCopyUtils.copyToByteArray(resource.getInputStream()), resource.getFilename());


        assertEquals(resource.getFilename(), imageData.getFileName());
        assertTrue(Arrays.equals(FileCopyUtils.copyToByteArray(resource.getInputStream()), imageData.getContent()));

    }

    @Test
    void addOrUpdateImage_updateImage() throws AppException, IOException {
        Resource resource1 = new ClassPathResource("images/testImage.jpg");
        Resource resource2 = new ClassPathResource("images/testImage2.jpg");

        User user = new User();
        ImageData imageData = new ImageData();
        imageData.setFileName(resource1.getFilename());
        imageData.setContent(FileCopyUtils.copyToByteArray(resource1.getInputStream()));
        user.setImageData(imageData);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).then(returnsFirstArg());

        ImageData imageData2 = userService.addOrUpdateImage(
                1L,
                FileCopyUtils.copyToByteArray(resource2.getInputStream()), resource2.getFilename());


        assertEquals(resource2.getFilename(), imageData2.getFileName());
        assertTrue(Arrays.equals(FileCopyUtils.copyToByteArray(resource2.getInputStream()), imageData2.getContent()));

    }

    @Test
    void downloadImage_whenIncorrectUserId_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> userService.downloadImage(1L));

        assertEquals(AppException.ErrorCodeMsg.USER_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void downloadImage() throws IOException, AppException {
        Resource resource1 = new ClassPathResource("images/testImage.jpg");
        User user = new User();
        ImageData imageData = new ImageData();
        imageData.setFileName(resource1.getFilename());
        imageData.setContent(FileCopyUtils.copyToByteArray(resource1.getInputStream()));
        user.setImageData(imageData);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        byte[] imageBytes = userService.downloadImage(1L);

        assertTrue(Arrays.equals(FileCopyUtils.copyToByteArray(resource1.getInputStream()), imageBytes));
    }

}