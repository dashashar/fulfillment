package ru.itis.fulfillment.impl.exception;

public class ForbiddenException extends AppException{
    public ForbiddenException(String errorMessage) {
        super(errorMessage);
    }
}
