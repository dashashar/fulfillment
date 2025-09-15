package ru.itis.fulfillment.impl.exception;

import lombok.Getter;
import org.springframework.web.client.HttpStatusCodeException;

@Getter
public class WildberriesApiException extends AppException{

    public WildberriesApiException(String errorMessage, HttpStatusCodeException cause) {
        super(errorMessage, cause);
    }
}
