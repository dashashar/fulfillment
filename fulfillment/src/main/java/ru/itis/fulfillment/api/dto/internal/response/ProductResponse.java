package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Ответ с информацией о товаре")
public record ProductResponse(
        @Schema(description = "Id товара", example = "10000420")
        long id,

        @Schema(description = "Наименование товара", example = "Кюлоты палаццо летние")
        String title,

        @Schema(description = "Баркод товара", example = "2037989779625")
        String barcode,

        @Schema(description = "Артикул товара", example = "кюлоты 400 черные")
        String article,

        @Schema(description = "Размер товара", example = "46")
        String size,

        @Schema(description = "Цвет товара", example = "черный")
        String color,

        @Schema(description = "Ссылка на первое фото товара на Wildberries",
                example = "https://basket-11.wbbasket.ru/vol1646/part164680/164680495/images/big/1.webp")
        String photoUrl
) {
}
