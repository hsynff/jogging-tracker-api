package com.jogging.tracker.service;

import com.jogging.tracker.model.dto.DataFilterRespDto;
import com.jogging.tracker.config.AppException;
import com.jogging.tracker.model.dto.RecordDto;
import com.jogging.tracker.model.dto.WeatherDto;
import com.jogging.tracker.model.entity.Record;
import com.jogging.tracker.model.entity.User;
import com.jogging.tracker.model.entity.WeatherCondition;
import com.jogging.tracker.repository.RecordRepository;
import com.jogging.tracker.repository.UserRepository;
import com.jogging.tracker.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static com.jogging.tracker.config.AppException.ErrorCodeMsg.RECORD_NOT_FOUND;
import static com.jogging.tracker.config.AppException.ErrorCodeMsg.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecordServiceImplTest {

    @InjectMocks
    @Spy
    private RecordServiceImpl recordService;

    @Mock
    private RecordRepository recordRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RSQLParser rsqlParser;
    @Mock
    private WeatherService weatherService;

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
        User userMock = Mockito.mock(User.class);
        WeatherDto weatherDtoMock = Mockito.mock(WeatherDto.class);
        Record recordMock = Mockito.mock(Record.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userMock));
        when(weatherService.getWeather(anyDouble(), anyDouble(), any(LocalDate.class))).thenReturn(weatherDtoMock);
        when(recordRepository.save(any(Record.class))).thenReturn(recordMock);

        Record record = recordService.create(LocalDate.now(), 1, 1, 1.0, 1.0, 1L);

        assertEquals(recordMock, record);
    }

    @Test
    void create_whenIncorrectUserId_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException appException = assertThrows(AppException.class,
                () -> recordService.create(LocalDate.now(), 1, 1, 1.0, 1.0, 1L));

        assertEquals(USER_NOT_FOUND.getMessage(), appException.getMessage());

    }

    @Test
    void create_whenWeatherServiceThrowsException_thenThrowException() throws AppException {
        User userMock = Mockito.mock(User.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userMock));
        when(weatherService.getWeather(anyDouble(), anyDouble(), any(LocalDate.class))).thenThrow(AppException.class);

        assertThrows(AppException.class,
                () -> recordService.create(LocalDate.now(), 1, 1, 1.0, 1.0, 1L));

    }


    @Test
    void getAll() throws AppException {
        User dummyUser = new User();
        dummyUser.setStatus(User.Status.ACTIVE);
        dummyUser.setRole(User.Role.ROLE_USER);
        Record dummyRecord1 = new Record();
        dummyRecord1.setUser(dummyUser);
        Record dummyRecord2 = new Record();
        dummyRecord2.setUser(dummyUser);

        Node nodeMock = Mockito.mock(Node.class);
        Specification<Record> specificationMock = Mockito.mock(Specification.class);

        when(rsqlParser.parse(anyString())).thenReturn(nodeMock);
        when(nodeMock.accept(any(CustomRsqlVisitor.class))).thenReturn(specificationMock);
        when(recordRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(new PageImpl<>(Arrays.asList(dummyRecord1, dummyRecord2)));

        DataFilterRespDto<RecordDto> respDto = recordService.getAll("distance eq 1", 0, 25, null);

        assertEquals(2, respDto.getTotalElements());
        assertEquals(1, respDto.getTotalPages());
        assertEquals(2, respDto.getData().size());
    }

    @Test
    void getById_whenWithoutUserAndCorrectRecordId_thenReturnRecord() throws AppException {
        Record recordMock = Mockito.mock(Record.class);

        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(recordMock));

        Record record = recordService.getById(1L, null);

        assertEquals(recordMock, record);
    }

    @Test
    void getById_whenWithCorrectUserIdAndCorrectRecordId_thenReturnRecord() throws AppException {
        Record recordMock = Mockito.mock(Record.class);

        User dummyUser = new User();
        dummyUser.setId(1L);

        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(recordMock));
        when(recordMock.getUser()).thenReturn(dummyUser);

        Record record = recordService.getById(1L, 1L);

        assertEquals(recordMock, record);
    }

    @Test
    void getById_whenIncorrectRecordId_thenThrowException() {
        when(recordRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> recordService.getById(1L, 2L));

        assertEquals(RECORD_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void getById_whenWithIncorrectUserId_thenThrowException() {
        Record recordMock = Mockito.mock(Record.class);

        User dummyUser = new User();
        dummyUser.setId(1L);

        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(recordMock));
        when(recordMock.getUser()).thenReturn(dummyUser);

        AppException ex = assertThrows(AppException.class, () -> recordService.getById(1L, 2L));

        assertEquals(RECORD_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void update_whenDateAndLatitudeOrLongitudeChanged_thenReturnRecordWithUpdatedWeather() throws AppException {
        WeatherDto dummyWeather = new WeatherDto();
        dummyWeather.setSummary("summary");
        dummyWeather.setTemperatureHigh(2.0);
        dummyWeather.setTemperatureLow(1.0);
        dummyWeather.setWindSpeed(3.0);
        dummyWeather.setZone("zone");

        Record dummyRecord = new Record();
        dummyRecord.setDate(LocalDate.of(2021, 11, 12));

        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(dummyRecord));
        when(weatherService.getWeather(anyDouble(), anyDouble(), any(LocalDate.class))).thenReturn(dummyWeather);
        when(recordRepository.save(any(Record.class))).thenReturn(dummyRecord);

        Record updatedRecord = recordService.update(
                1L,
                LocalDate.of(2021, 11, 11),
                null,
                null,
                3.0,
                4.0,
                null);

        assertEquals(2021, updatedRecord.getDate().getYear());
        assertEquals(11, updatedRecord.getDate().getMonthValue());
        assertEquals(11, updatedRecord.getDate().getDayOfMonth());
        assertEquals(3.0, updatedRecord.getLatitude());
        assertEquals(4.0, updatedRecord.getLongitude());

        assertNotNull(updatedRecord.getWeather());

        WeatherCondition weather = updatedRecord.getWeather();
        assertEquals("summary", weather.getSummary());
        assertEquals(2.0, weather.getTemperatureHigh());
        assertEquals(1.0, weather.getTemperatureLow());
        assertEquals(3.0, weather.getWindSpeed());
        assertEquals("zone", weather.getZone());

    }

    @Test
    void update_whenIncorrectUserId_thenThrowException() {
        Record recordMock = Mockito.mock(Record.class);

        User dummyUser = new User();
        dummyUser.setId(1L);

        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(recordMock));
        when(recordMock.getUser()).thenReturn(dummyUser);

        AppException ex = assertThrows(AppException.class,
                () -> recordService.update(1L, LocalDate.now(), 1, 1, 1.0, 1.0, 2L));

        assertEquals(RECORD_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void update_whenIncorrectRecordId_thenThrowException() {
        when(recordRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> recordService.update(1L, LocalDate.now(), 1, 1, 1.0, 1.0, null));

        assertEquals(RECORD_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void delete_whenIncorrectRecordId_thenThrowException() {
        when(recordRepository.findById(anyLong())).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class,
                () -> recordService.delete(1L, null));

        assertEquals(RECORD_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void delete_whenIncorrectUserId_thenThrowException() {
        Record recordMock = Mockito.mock(Record.class);

        User dummyUser = new User();
        dummyUser.setId(1L);

        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(recordMock));
        when(recordMock.getUser()).thenReturn(dummyUser);

        AppException ex = assertThrows(AppException.class,
                () -> recordService.delete(1L, 2L));

        assertEquals(RECORD_NOT_FOUND.getMessage(), ex.getMessage());
    }

    @Test
    void delete() throws AppException {
        Record recordMock = Mockito.mock(Record.class);

        User dummyUser = new User();
        dummyUser.setId(1L);

        when(recordRepository.findById(anyLong())).thenReturn(Optional.of(recordMock));
        when(recordMock.getUser()).thenReturn(dummyUser);

        Record deletedRecord = recordService.delete(1L, 1L);
        assertEquals(recordMock, deletedRecord);
    }

    @Test
    void isLocationChangeExceeds_whenLatitudeMoreThanLimit_thenReturnTrue() {
        boolean locationChangeExceeds = recordService
                .isLocationChangeExceeds(1.0, null, 5.0, null, 3.0);

        assertTrue(locationChangeExceeds);
    }

    @Test
    void isLocationChangeExceeds_whenLongitudeMoreThanLimit_thenReturnTrue() {
        boolean locationChangeExceeds = recordService
                .isLocationChangeExceeds(null, 1.0, null, 5.0, 3.0);

        assertTrue(locationChangeExceeds);
    }

}