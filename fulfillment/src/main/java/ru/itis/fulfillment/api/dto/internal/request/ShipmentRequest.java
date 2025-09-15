package ru.itis.fulfillment.api.dto.internal.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на создание отгрузки")
public record ShipmentRequest(
        @Schema(description = "Количество товара", example = "50")
        @NotNull(message = "The quantity cannot be empty")
        @Positive(message = "The number must be greater than 0")
        Integer quantity,

        @Schema(description = "Id склада продавца на Wildberries", example = "751703")
        @NotNull(message = "The warehouse id cannot be empty")
        @Min(value = 0, message = "The warehouse id cannot be negative")
        Long warehouseId,

        @Schema(description = "Название склада продавца на Wildberries", example = "Казань")
        @NotBlank(message = "The warehouse name cannot be empty")
        @Size(min = 1, max = 255, message = "The warehouse name must be between 1 and 255 characters long")
        String warehouseName,

        @Schema(description = "Id фулфилмента", example = "10006115")
        @NotNull(message = "The fulfillment id cannot be empty")
        @Min(value = 0, message = "The fulfillment id cannot be negative")
        Long fulfillmentId
) {
}
