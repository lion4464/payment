package com.rustambek.payment.exception;

import com.rustambek.payment.utils.ErrorMessageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> apiException(ApiException e, ServletWebRequest webRequest) {
        log.error(e.getMessage(), e);
        return ResponseEntity
                .status(e.getHttpStatus())
                .body(new ErrorResponse(
                                e.getHttpStatus().value(),
                                e.getClass().getSimpleName(),
                                webRequest.getRequest().getRequestURI(),
                                e.getLocalizedMessage(),
                                e.getData())
                                );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(
            MethodArgumentNotValidException ex, ServletWebRequest webRequest) {

        String returnAllerrorsValue = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        log.error("Validation failed: {}", returnAllerrorsValue, ex);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        ex.getClass().getSimpleName(),
                        webRequest.getRequest().getRequestURI(),
                        returnAllerrorsValue,
                        new Date())
                );
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception, ServletWebRequest request) {

        if (exception.getMessage().startsWith("Service unavailable")) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new ErrorResponse(
                            HttpStatus.SERVICE_UNAVAILABLE.value(),
                            exception.getClass().getSimpleName(),
                            request.getRequest().getRequestURI(),
                            ErrorMessageUtil.getErrorMessage(exception),
                            new Date())
                    );
             }

        if (exception.getMessage().equals("Access is denied") || exception.getMessage().equals("Доступ запрещен")) {
            log.info("##########################USER HAS NOT PERMISSION#############################" + exception.getMessage());
            return ResponseEntity
                    .status(HttpStatus.EXPECTATION_FAILED)
                    .body(new ErrorResponse(
                            HttpStatus.EXPECTATION_FAILED.value(),
                            exception.getClass().getSimpleName(),
                            request.getRequest().getRequestURI(),
                            ErrorMessageUtil.getErrorMessage(exception),
                            new Date())
                    );
              } else
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            exception.getClass().getSimpleName(),
                            request.getRequest().getRequestURI(),
                            ErrorMessageUtil.getErrorMessage(exception),
                            new Date())
                    );
             }

}
