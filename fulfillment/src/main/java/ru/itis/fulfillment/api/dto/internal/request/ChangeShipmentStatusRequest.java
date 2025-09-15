package ru.itis.fulfillment.api.dto.internal.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Запрос на обновление статуса отгрузки")
public record ChangeShipmentStatusRequest(
        @Schema(description = "Новое значение статуса", example = "В работе")
        @NotBlank(message = "The status cannot be empty")
        @Size(max = 50, message = "The status must not exceed 50 characters")
        String status
) {
}
