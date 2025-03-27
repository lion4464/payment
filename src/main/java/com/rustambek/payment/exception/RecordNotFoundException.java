package com.rustambek.payment.exception;

import org.springframework.http.HttpStatus;

public class RecordNotFoundException extends ApiException{

    public RecordNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
