package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с информацией о складе")
public record WarehouseResponse(
        @Schema(description = "Id склада продавца на Wildberries", example = "751703")
        Integer id,

        @Schema(description = "Название склада продавца на Wildberries", example = "Казань")
        String name
) {
}
