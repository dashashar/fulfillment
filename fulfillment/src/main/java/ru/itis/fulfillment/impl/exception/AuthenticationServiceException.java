package ru.itis.fulfillment.impl.exception;

public class AuthenticationServiceException extends AppException{
    public AuthenticationServiceException(String errorMessage) {
        super(errorMessage);
    }

    public AuthenticationServiceException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
