package com.jogging.tracker.util;

import com.jogging.tracker.config.AppException;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.UnknownOperatorException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.YearMonth;

import static com.jogging.tracker.config.AppException.ErrorCodeMsg.*;
import static org.junit.jupiter.api.Assertions.*;

class CommonUtilsTest {

    @Test
    void fromDateString() throws AppException {
        LocalDate localDate = CommonUtils.fromDateString("2021-02-02");

        assertNotNull(localDate);
        assertEquals(2021, localDate.getYear());
        assertEquals(2, localDate.getMonthValue());
        assertEquals(2, localDate.getDayOfMonth());
    }

    @Test
    void fromDateString_whenNullInput_thenReturnNull() throws AppException {
        LocalDate localDate = CommonUtils.fromDateString(null);

        assertNull(localDate);
    }

    @Test
    void fromDateString_whenIncorrectDateFormat_thenThrowException() {
        AppException ex = assertThrows(AppException.class,
                () -> CommonUtils.fromDateString("02-02-2021"));

        assertEquals(DATE_PARSE_ERROR.getMessage(), ex.getMessage());

    }

    @Test
    void fromYearMonthString() throws AppException {
        YearMonth yearMonth = CommonUtils.fromYearMonthString("2021-02");

        assertNotNull(yearMonth);
        assertEquals(2021, yearMonth.getYear());
        assertEquals(2, yearMonth.getMonthValue());
    }

    @Test
    void fromYearMonthString_whenNullInput_thenReturnNull() throws AppException {
        YearMonth yearMonth = CommonUtils.fromYearMonthString(null);

        assertNull(yearMonth);
    }

    @Test
    void fromYearMonthString_whenIncorrectDateFormat_thenThrowException() {
        AppException ex = assertThrows(AppException.class,
                () -> CommonUtils.fromYearMonthString("2021-02-02"));

        assertEquals(YEAR_MONTH_PARSE_ERROR.getMessage(), ex.getMessage());
    }

    @Test
    void toDateString() {
        String date = CommonUtils.toDateString(LocalDate.of(2021, 1, 1));

        assertNotNull(date);
        assertEquals("2021-01-01", date);
    }

    @Test
    void translateToAppException_whenNotInstanceOfUnknownOperatorException_thenReturnAppExceptionWithoutAdditionalMessage() {
        RSQLParserException rsqlParserException = new RSQLParserException(new IllegalArgumentException(""));

        AppException appException = CommonUtils.translateToAppException(rsqlParserException);

        assertNull(appException.getAdditionalMessage());
    }

    @Test
    void translateToAppException_whenInstanceOfUnknownOperatorException_thenReturnAppExceptionWithAdditionalMessage() {
        RSQLParserException rsqlParserException = new RSQLParserException(new UnknownOperatorException("TEST"));

        AppException appException = CommonUtils.translateToAppException(rsqlParserException);

        assertNotNull(appException.getAdditionalMessage());
    }

}