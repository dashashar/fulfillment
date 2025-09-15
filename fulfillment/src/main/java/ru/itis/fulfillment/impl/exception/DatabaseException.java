package ru.itis.fulfillment.impl.exception;

public class DatabaseException extends AppException{
    public DatabaseException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
