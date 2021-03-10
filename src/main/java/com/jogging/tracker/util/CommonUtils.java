package com.jogging.tracker.util;

import com.jogging.tracker.config.AppException;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.UnknownOperatorException;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Slf4j
public class CommonUtils {

    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String YEAR_MONTH_FORMAT = "yyyy-MM";

    public static LocalDate fromDateString(String date) throws AppException {
        if (date == null) {
            return null;
        }

        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(DATE_FORMAT);
            return LocalDate.parse(date, dtf);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AppException.ErrorCodeMsg.DATE_PARSE_ERROR);
        }
    }

    public static YearMonth fromYearMonthString(String yearMonth) throws AppException {
        if (yearMonth == null) {
            return null;
        }

        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern(YEAR_MONTH_FORMAT);
            return YearMonth.parse(yearMonth, dtf);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new AppException(AppException.ErrorCodeMsg.YEAR_MONTH_PARSE_ERROR);
        }
    }

    public static String toDateString(LocalDate date) {
        return date == null ? null : date.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
    }

    public static AppException translateToAppException(Exception e) {
        if (e instanceof RSQLParserException && !(e.getCause() instanceof UnknownOperatorException)) {
            return new AppException(AppException.ErrorCodeMsg.QUERY_PARSE_ERROR);
        }
        return new AppException(AppException.ErrorCodeMsg.QUERY_PARSE_ERROR, e.getCause().getMessage());
    }
}
