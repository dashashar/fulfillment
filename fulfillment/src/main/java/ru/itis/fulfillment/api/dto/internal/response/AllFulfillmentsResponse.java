package ru.itis.fulfillment.api.dto.internal.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Ответ на получение фулфилментов")
public record AllFulfillmentsResponse(
        @Schema(description = "Список фулфилментов")
        List<FulfillmentResponse> fulfillments,

        @Schema(description = "Количество фулфилментов в ответе", example = "1")
        int size,

        @Schema(description = "Наличие фулфилментов на следующей странице", example = "false")
        boolean hasNext
) {
}
