package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ru.itis.fulfillment.impl.util.DateTimeUtils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Schema(description = "Ответ на обновление")
public record UpdateResponse(

        @Schema(description = "Дата и время обновления", example = "2025-07-28T20:43:27.13Z")
        String timestamp,

        @Schema(description = "Сообщение об успехе", example = "Successfully updated the products")
        String message
) {
    public UpdateResponse(String message) {
        this(DateTimeUtils.formatToIsoWithShortMillis(OffsetDateTime.now(ZoneOffset.UTC)), message);
    }
}
