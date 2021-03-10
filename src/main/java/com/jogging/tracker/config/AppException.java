package com.jogging.tracker.config;

import com.jogging.tracker.util.CommonUtils;

public class AppException extends Exception {
    private ErrorCodeMsg errorCodeMsg;
    private String additionalMessage;

    public AppException(ErrorCodeMsg errorCodeMsg) {
        this.errorCodeMsg = errorCodeMsg;
    }


    public AppException(ErrorCodeMsg errorCodeMsg, String additionalMessage) {
        this.errorCodeMsg = errorCodeMsg;
        this.additionalMessage = additionalMessage;
    }

    public int getCode() {
        return this.errorCodeMsg.code;
    }

    public String getMessage() {
        return this.errorCodeMsg.message;
    }

    public String getAdditionalMessage() {
        return additionalMessage;
    }

    public enum ErrorCodeMsg {
        INTERNAL(1000, "Internal Error"),
        USER_ALREADY_EXISTS(1001, "User with given email already exists. Please choose different one"),
        USER_ALREADY_VERIFIED(1002, "User account already verified"),
        USER_NOT_FOUND(1003, "Could not find user with given parameters"),
        REQUEST_PARAMETERS_NOT_VALID(1004, "One or more mandatory fields could not be validated"),
        USER_CREDENTIALS_NOT_VALID(1005, "Incorrect email/password combination"),
        USER_NOT_VERIFIED(1006, "User email is not verified. Please check your email and verify your account"),
        USER_BLOCKED(1007, "User is blocked. Please contact to administrators"),
        DATE_PARSE_ERROR(1008, String.format("Could not parse given date. Should be in '%s' format", CommonUtils.DATE_FORMAT)),
        YEAR_MONTH_PARSE_ERROR(1009, String.format("Could not parse given date. Should be in '%s' format", CommonUtils.YEAR_MONTH_FORMAT)),
        DATE_SLOT_ERROR(1010, "Date should not be in future"),
        RECORD_NOT_FOUND(1011, "Could not find record with given parameters"),
        QUERY_PARSE_ERROR(1012, "Could not parse given query"),
        WEATHER_API_ERROR(1013, "Could not get data from remote weather API"),
        INVALID_JWT_TOKEN(1014, "Invalid token"),
        MAX_FILE_UPLOAD_SIZE_EXCEED(1015, "File size exceeds maximum limit"),
        INVALID_FILE_CONTENT_TYPE(1016, "Invalid file type. Only image files allowed");

        ErrorCodeMsg(int code, String message) {
            this.code = code;
            this.message = message;
        }

        private int code;
        private String message;

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }

}

