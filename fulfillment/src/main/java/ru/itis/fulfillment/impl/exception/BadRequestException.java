package ru.itis.fulfillment.impl.exception;

public class BadRequestException extends AppException{
    public BadRequestException(String errorMessage) {
        super(errorMessage);
    }
}
