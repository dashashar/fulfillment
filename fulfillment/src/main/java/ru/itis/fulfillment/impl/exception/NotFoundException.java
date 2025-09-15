package ru.itis.fulfillment.impl.exception;

public class NotFoundException extends AppException{

    public NotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
