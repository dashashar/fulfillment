package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с информацией о фулфилменте")
public record FulfillmentResponse(
        @Schema(description = "Id фулфилмента", example = "10006115")
        long id,

        @Schema(description = "Дата и время создания фулфилмента", example = "2025-07-28T19:08:58.22Z")
        String date,

        @Schema(description = "Наименование товара", example = "Костюм шорты рубашка летний")
        String title,

        @Schema(description = "Баркод товара", example = "2037991665923")
        String barcode,

        @Schema(description = "Артикул товара", example = "костюм с шортами 850 бежевый")
        String article,

        @Schema(description = "Размер товара", example = "L")
        String size,

        @Schema(description = "Цвет товара", example = "бежевый")
        String color,

        @Schema(description = "Ссылка на первое фото товара на Wildberries",
                example = "https://basket-11.wbbasket.ru/vol1647/part164710/164710105/images/big/1.webp")
        String photoUrl,

        @Schema(description = "Количество товара", example = "200")
        int quantity,

        @Schema(description = "Техническое задание", example = "Проверить на брак, упаковать в пупырчатую пленку")
        String taskDescription
) {
}
