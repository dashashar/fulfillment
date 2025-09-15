package ru.itis.fulfillment.impl.exception;

public class GoogleSheetsInitializationException extends RuntimeException {

    public GoogleSheetsInitializationException(String errorMessage) {
        super(errorMessage);
    }

    public GoogleSheetsInitializationException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }

}
