package com.jogging.tracker.config;

import com.jogging.tracker.model.dto.ApiError;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.jogging.tracker.config.AppException.ErrorCodeMsg.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final int CUSTOM_HTTP_ERROR_CODE = 520;

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiError> handleApiException(AppException e) {
        log.error(e.getMessage(), e);

        ApiError apiError = new ApiError();
        apiError.setCode(e.getCode());
        apiError.setMessage(e.getMessage());
        apiError.setAdditionalMessage(e.getAdditionalMessage());

        return ResponseEntity
                .status(CUSTOM_HTTP_ERROR_CODE)
                .body(apiError);

    }

    @ExceptionHandler(SizeLimitExceededException.class)
    public ResponseEntity<ApiError> handleSizeLimitException(SizeLimitExceededException e) {
        log.error(e.getMessage(), e);

        AppException.ErrorCodeMsg uploadSizeExceed = MAX_FILE_UPLOAD_SIZE_EXCEED;

        ApiError apiError = new ApiError();
        apiError.setCode(uploadSizeExceed.getCode());
        apiError.setMessage(uploadSizeExceed.getMessage());
        apiError.setAdditionalMessage("Max size should be: " + maxFileSize);


        return ResponseEntity
                .status(CUSTOM_HTTP_ERROR_CODE)
                .body(apiError);
    }


    @Override
    protected ResponseEntity handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleFieldValidationErrorInternal(Collections.singletonList(ex.getParameterName()));
    }

    @Override
    protected ResponseEntity handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleFieldValidationErrorInternal(ex);
    }

    @Override
    protected ResponseEntity handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return handleFieldValidationErrorInternal(ex);
    }

    private ResponseEntity<ApiError> handleFieldValidationErrorInternal(List<String> errorFields) {
        AppException.ErrorCodeMsg requestParametersNotValid = REQUEST_PARAMETERS_NOT_VALID;

        ApiError apiError = new ApiError();
        apiError.setCode(requestParametersNotValid.getCode());
        apiError.setMessage(requestParametersNotValid.getMessage());

        apiError.setErrorFields(errorFields);

        return ResponseEntity
                .status(CUSTOM_HTTP_ERROR_CODE)
                .body(apiError);
    }

    private ResponseEntity<ApiError> handleFieldValidationErrorInternal(BindException ex) {
        log.error(ex.getMessage(), ex);

        return handleFieldValidationErrorInternal(
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(FieldError::getField)
                        .collect(Collectors.toList())
        );

    }
}
