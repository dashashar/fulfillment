package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с информацией о пользователе")
public record AccountResponse(
        @Schema(description = "id пользователя", example = "100050")
        long id,

        @Schema(description = "Имя пользователя", example = "Anna")
        String name,

        @Schema(description = "Номер телефона пользователя", example = "+79123456789")
        String phone,

        @Schema(description = "Email пользователя", example = "anna@example.com")
        String email
) {
}
