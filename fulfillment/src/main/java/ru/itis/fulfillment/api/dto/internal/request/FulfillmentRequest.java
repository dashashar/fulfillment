package ru.itis.fulfillment.api.dto.internal.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на создание фулфилмента")
public record FulfillmentRequest (
        @Schema(description = "id товара", example = "10000330")
        @NotNull(message = "The product id cannot be empty")
        @Min(value = 0, message = "The fulfillment id cannot be negative")
        Long productId,

        @Schema(description = "Количество", example = "200")
        @NotNull(message = "The quantity cannot be empty")
        @Positive(message = "The number must be greater than 0")
        Integer quantity,

        @Schema(description = "Техническое задание", example = "Проверить на брак, упаковать в пупырчатую пленку")
        @NotBlank(message = "The task description cannot be empty")
        String taskDescription

) {
}
