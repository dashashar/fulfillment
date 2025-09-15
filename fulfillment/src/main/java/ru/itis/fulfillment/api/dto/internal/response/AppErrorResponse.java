package ru.itis.fulfillment.api.dto.internal.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import ru.itis.fulfillment.impl.util.DateTimeUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Schema(description = "Стандартный ответ ошибки")
public record AppErrorResponse(

        @Schema(description = "Дата и время, когда произошла ошибка", example = "2025-07-28T11:51:39.39Z")
        String timestamp,

        @Schema(description = "Статусный код HTTP", example = "400")
        int status,

        @Schema(description = "Заголовок ошибки", example = "Validation error")
        String error,

        @Schema(description = "Описание ошибки", example = "Incorrect data was entered", nullable = true)
        String message,

        @Schema(description = "Путь запроса, где произошла ошибка", example = "/api/v1/shipment")
        String path,

        @Schema(description = "Дополнительные сведения при ошибках валидации",
                example = """
                        {
                                 "quantity": "The number must be greater than 0",
                                 "warehouseId": "The warehouse id cannot be empty",
                                 "warehouseName": "The warehouse name cannot be empty"
                        }""",
                nullable = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        Map<String, String> details
) {
    public AppErrorResponse(HttpStatus status, String error, String message, String path) {
        this(DateTimeUtils.formatToIsoWithShortMillis(OffsetDateTime.now(ZoneOffset.UTC)), status.value(), error, message, path, null);
    }

    public AppErrorResponse(HttpStatus status, String error, String message, String path, Map<String, String> details) {
        this(DateTimeUtils.formatToIsoWithShortMillis(OffsetDateTime.now(ZoneOffset.UTC)), status.value(), error, message, path, details);
    }

    public AppErrorResponse(OffsetDateTime timestamp, int status, String error, String message, String path) {
        this(DateTimeUtils.formatToIsoWithShortMillis(timestamp), status, error, message, path, null);
    }

    public AppErrorResponse(int status, String error, String message, String path) {
        this(DateTimeUtils.formatToIsoWithShortMillis(OffsetDateTime.now(ZoneOffset.UTC)), status, error, message, path, null);
    }
}