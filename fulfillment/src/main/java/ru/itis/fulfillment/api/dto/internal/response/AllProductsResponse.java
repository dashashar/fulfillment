package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ответ на получение товаров")
public record AllProductsResponse (
        @Schema(description = "Список товаров")
        List<ProductResponse> products,

        @Schema(description = "Количество товаров в ответе", example = "1")
        int size,

        @Schema(description = "Наличие товаров на следующей странице", example = "false")
        boolean hasNext
) {
}
