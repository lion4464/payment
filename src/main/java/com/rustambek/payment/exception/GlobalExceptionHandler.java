package com.rustambek.payment.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
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

}
