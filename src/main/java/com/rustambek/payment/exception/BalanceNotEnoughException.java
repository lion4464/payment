package com.rustambek.payment.exception;

import org.springframework.http.HttpStatus;

public class BalanceNotEnoughException extends ApiException {
    public BalanceNotEnoughException(String message) {
        super(message);
    }
}
