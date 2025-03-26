package com.rustambek.payment.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
@Setter
@Getter
public class ApiException extends RuntimeException{
    private HttpStatus httpStatus;
    private Object data;
    public ApiException(String message, HttpStatus httpStatus) {
        super(message);
        setHttpStatus(httpStatus);
    }
    public ApiException(String message) {
        super(message);
        setHttpStatus(HttpStatus.BAD_REQUEST);
    }
}
