package com.rustambek.payment.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends ApiException{
    public TokenExpiredException(String message) {
        super(message);
    }
}
