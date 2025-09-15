package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с значением api ключа")
public record KeyResponse(
        @Schema(description = "Значение api ключа", example = "NC1hZTVjLTcwZDYtODBjMy1kMTA1...")
        String wbApiKey
) {
}
