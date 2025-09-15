package ru.itis.fulfillment.api.dto.internal.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на обновление api ключа")
public record ChangeKeyRequest(
        @Schema(description = "Новое значение api ключа", example = "NC1hZTVjLTcwZDYtODBjMy1kMTA1...")
        @NotBlank(message = "The wb api key cannot be empty")
        @Size(max = 500, message = "The wb Api Key must not exceed 500 characters")
        String wbApiKey
) {
}
