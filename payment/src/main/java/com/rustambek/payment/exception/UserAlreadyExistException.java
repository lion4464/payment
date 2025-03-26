package com.rustambek.payment.exception;



public class UserAlreadyExistException extends ApiException {
    public UserAlreadyExistException(String username) {
        super(String.format("User %s already exist", username));
    }
}
