package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ на обновление api ключа")
public record ChangeKeyResponse(
        @Schema(description = "Сообщение об успешном обновлении", example = "Wb api key has been successfully updated")
        String message,

        @Schema(description = "Окончание нового api ключа", example = "***DQ4OX0")
        String newKeyLastChars
) {
}
