package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ответ на получение отгрузок")
public record AllShipmentsResponse(
        @Schema(description = "Список отгрузок")
        List<ShipmentResponse> shipments,

        @Schema(description = "Количество отгрузок в ответе", example = "1")
        int size,

        @Schema(description = "Наличие отгрузок на следующей странице", example = "false")
        boolean hasNext
) {
}
