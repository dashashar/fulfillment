package ru.itis.fulfillment.api.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itis.fulfillment.api.dto.internal.request.FulfillmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.AllFulfillmentsResponse;
import ru.itis.fulfillment.api.dto.internal.response.AppErrorResponse;
import ru.itis.fulfillment.api.dto.internal.response.FulfillmentResponse;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;

@Tag(name = "Fulfillment API", description = "API для управления фулфилментами")
@SecurityRequirement(name = "TelegramAuth")
@RequestMapping("/api/v1/fulfillment")
public interface FulfillmentApi {

    @Operation(
            summary = "Создание фулфилмента",
            description = "Сохраняет новый фулфилмент и отправляет данные в гугл таблицу",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успешное сохранение",
                            content = @Content(schema = @Schema(implementation = FulfillmentResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T18:42:51.70Z",
                                                         "status": 400,
                                                         "error": "Validation error",
                                                         "message": "Incorrect data was entered",
                                                         "path": "/api/v1/fulfillment",
                                                         "details": {
                                                             "quantity": "The number must be greater than 0",
                                                             "productId": "The product id cannot be empty"
                                                         }
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован через telegram",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:28:24.68Z",
                                                        "status": 401,
                                                        "error": "Telegram authentication failed",
                                                        "message": "Init data expired. Log in again via Telegram",
                                                        "path": "/api/v1/fulfillment"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "403", description = "Доступ к товару запрещен",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T18:50:50.54Z",
                                                         "status": 403,
                                                         "error": "Access denied",
                                                         "message": "There is no access to this product",
                                                         "path": "/api/v1/fulfillment"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Товар не найден",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T18:48:20.03Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Product with id: 100152101 not found",
                                                         "path": "/api/v1/fulfillment"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:22:31.00Z",
                                                        "status": 500,
                                                        "error": "Database error",
                                                        "message": "Couldn't save fulfillment",
                                                        "path": "/api/v1/fulfillment"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "502", description = "Ошибка при работе с api Google Sheets",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T11:13:43.69Z",
                                                        "status": 502,
                                                        "error": "Google Sheets API error",
                                                        "message": "Couldn't export to Google Sheets, so the fulfillment was not saved",
                                                        "path": "/api/v1/fulfillment"
                                                    }"""
                                    )))
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    FulfillmentResponse createFulfillment(@AuthenticationPrincipal AccountPrincipal account,
                                          @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                                  description = "Данные для создания фулфилмента",
                                                  required = true,
                                                  content = @Content(mediaType = "application/json"))
                                          @org.springframework.web.bind.annotation.RequestBody @Valid FulfillmentRequest request);

    @Operation(
            summary = "Получение фулфилмента",
            description = "Возвращает информацию о фулфилменте",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = FulfillmentResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:18:03.118+00:00",
                                                         "status": 400,
                                                         "error": "Bad Request",
                                                         "path": "/api/v1/fulfillment/abcd"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован через telegram",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:28:24.68Z",
                                                        "status": 401,
                                                        "error": "Telegram authentication failed",
                                                        "message": "Init data expired. Log in again via Telegram",
                                                        "path": "/api/v1/fulfillment/10000005"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "403", description = "Доступ к фулфилменту запрещен",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:21:10.74Z",
                                                         "status": 403,
                                                         "error": "Access denied",
                                                         "message": "There is no access to this fulfillment",
                                                         "path": "/api/v1/fulfillment/10000005"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Фулфилмент не найден",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:21:45.21Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Fulfillment with id: 100000 not found",
                                                         "path": "/api/v1/fulfillment/100000"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:22:31.00Z",
                                                        "status": 500,
                                                        "error": "Database error",
                                                        "message": "Couldn't get fulfillment",
                                                        "path": "/api/v1/fulfillment/10000005"
                                                    }"""
                                    )))
            })
    @GetMapping("/{fulfillmentId}")
    @ResponseStatus(HttpStatus.OK)
    FulfillmentResponse getFulfillment(@AuthenticationPrincipal AccountPrincipal account,
                                       @Parameter(
                                               name = "fulfillmentId",
                                               description = "Id фулфилмента",
                                               required = true,
                                               in = ParameterIn.PATH,
                                               schema = @Schema(type = "integer", format = "int64", example = "10000005")
                                       )
                                       @PathVariable long fulfillmentId);

    @Operation(
            summary = "Поиск фулфилментов",
            description = "Возвращает список фулфилментов пользователя с фильтром и пагинацией в отсортированном по дате виде (сначала новые)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = AllFulfillmentsResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:34:23.56Z",
                                                         "status": 400,
                                                         "error": "Bad request",
                                                         "message": "The limit cannot exceed 30",
                                                         "path": "/api/v1/fulfillment"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован через telegram",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:28:24.68Z",
                                                        "status": 401,
                                                        "error": "Telegram authentication failed",
                                                        "message": "Init data expired. Log in again via Telegram",
                                                        "path": "/api/v1/fulfillment"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:22:31.00Z",
                                                        "status": 500,
                                                        "error": "Database error",
                                                        "message": "Couldn't get fulfillments",
                                                        "path": "/api/v1/fulfillment"
                                                    }"""
                                    )))
            })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    AllFulfillmentsResponse getAllFulfillments(@AuthenticationPrincipal AccountPrincipal account,

                                               @Parameter(
                                                       name = "search",
                                                       description = "Строка для поиска фулфилментов по началу наименования, артикула или баркода товара",
                                                       in = ParameterIn.QUERY,
                                                       example = "костюм"
                                               )
                                               @RequestParam(required = false) String search,

                                               @Parameter(
                                                       name = "page",
                                                       description = "Номер страницы (начинается с 0)",
                                                       in = ParameterIn.QUERY,
                                                       schema = @Schema(type = "integer", minimum = "0"),
                                                       example = "0"
                                               )
                                               @RequestParam(defaultValue = "0") int page,

                                               @Parameter(
                                                       name = "limit",
                                                       description = "Количество элементов на странице",
                                                       in = ParameterIn.QUERY,
                                                       schema = @Schema(type = "integer", minimum = "1", maximum = "30"),
                                                       example = "5"
                                               )
                                               @RequestParam(defaultValue = "15") int limit);

}
