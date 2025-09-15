package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с информацией об отгрузке")
public record ShipmentResponse(
        @Schema(description = "Id отгрузки", example = "10006115")
        long id,

        @Schema(description = "Дата и время создания отгрузки", example = "2025-07-28T21:17:17.80Z")
        String date,

        @Schema(description = "Количество товара", example = "50")
        int quantity,

        @Schema(description = "Id склада продавца на Wildberries", example = "751703")
        long warehouseId,

        @Schema(description = "Название склада продавца на Wildberries", example = "Казань")
        String warehouseName,

        @Schema(description = "Статус отгрузки (при создании равен null)", example = "Отгружено")
        String status,

        @Schema(description = "Id фулфилмента", example = "10006115")
        long fulfillmentId,

        @Schema(description = "Наименование товара", example = "Костюм шорты рубашка летний")
        String title,

        @Schema(description = "Баркод товара", example = "2037991665923")
        String barcode,

        @Schema(description = "Артикул товара", example = "костюм с шортами 850 бежевый")
        String article,

        @Schema(description = "Размер товара", example = "L")
        String size,

        @Schema(description = "Цвет товара", example = "бежевый")
        String color
) {
}
