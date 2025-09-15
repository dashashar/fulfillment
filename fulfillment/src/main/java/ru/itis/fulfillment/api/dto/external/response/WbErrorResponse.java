package ru.itis.fulfillment.api.dto.external.response;

import lombok.Data;

@Data
public class WbErrorResponse {
    private String title;
    private String detail;
    private String timestamp;
    private String message;
    private String errorText;
}
