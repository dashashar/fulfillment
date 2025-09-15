package ru.itis.fulfillment.impl.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GoogleSheetsException extends AppException{

    private String userMessage;

    public GoogleSheetsException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleSheetsException withUserMessage(String userMessage) {
        this.userMessage = userMessage;
        return this;
    }

}
