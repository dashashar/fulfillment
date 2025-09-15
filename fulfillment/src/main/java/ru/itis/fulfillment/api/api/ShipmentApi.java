package ru.itis.fulfillment.api.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import ru.itis.fulfillment.api.dto.internal.request.ChangeShipmentStatusRequest;
import ru.itis.fulfillment.api.dto.internal.request.ShipmentRequest;
import ru.itis.fulfillment.api.dto.internal.response.*;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;

import java.util.List;

@Tag(name = "Shipment API", description = "API для управления отгрузками")
@RequestMapping("/api/v1/shipment")
public interface ShipmentApi {

    @SecurityRequirement(name = "TelegramAuth")
    @Operation(
            summary = "Получение складов продавца",
            description = "Возвращает список складов продавца, делая запрос к api Wildberries",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(array = @ArraySchema(
                                    schema = @Schema(implementation = WarehouseResponse.class)), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Недействительный api ключ Wildberries",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:04:22.00Z",
                                                         "status": 400,
                                                         "error": "Wildberries API Error: unauthorized",
                                                         "message": "token problem; token is malformed: token contains an invalid number of segments",
                                                         "path": "/api/v1/shipment/warehouses"
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
                                                        "path": "/api/v1/shipment/warehouses"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "502", description = "Ошибка при работе с api Wildberries",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T21:07:26.39Z",
                                                         "status": 502,
                                                         "error": "Wildberries API Error",
                                                         "message": "Incorrect required body",
                                                         "path": "/api/v1/shipment/warehouses"
                                                    }"""
                                    )))
            })
    @GetMapping("/warehouses")
    @ResponseStatus(HttpStatus.OK)
    List<WarehouseResponse> getWarehouses(@AuthenticationPrincipal AccountPrincipal account);

    @SecurityRequirement(name = "TelegramAuth")
    @Operation(
            summary = "Создание отгрузки",
            description = "Сохраняет новую отгрузку, привязанную к фулфилменту, и отправляет данные в гугл таблицу",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успешное сохранение",
                            content = @Content(schema = @Schema(implementation = ShipmentResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован через telegram",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:28:24.68Z",
                                                        "status": 401,
                                                        "error": "Telegram authentication failed",
                                                        "message": "Init data expired. Log in again via Telegram",
                                                        "path": "/api/v1/shipment"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "403", description = "Доступ к фулфилменту запрещен",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T21:11:38.42Z",
                                                         "status": 403,
                                                         "error": "Access denied",
                                                         "message": "There is no access to this fulfillment",
                                                         "path": "/api/v1/shipment"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Фулфилмент не найден",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T21:12:21.40Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Fulfillment with id: 1000000 not found",
                                                         "path": "/api/v1/shipment"
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
                                                        "message": "Couldn't save shipment",
                                                        "path": "/api/v1/shipment"
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
                                                        "message": "Couldn't export to Google Sheets, so the shipment was not saved",
                                                        "path": "/api/v1/shipment"
                                                    }"""
                                    )))
            })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    ShipmentResponse createShipment(@AuthenticationPrincipal AccountPrincipal account,
                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                            description = "Данные для создания отгрузки",
                                            required = true,
                                            content = @Content(mediaType = "application/json"))
                                    @org.springframework.web.bind.annotation.RequestBody @Valid ShipmentRequest request);

    @SecurityRequirement(name = "TelegramAuth")
    @Operation(
            summary = "Получение отгрузки",
            description = "Возвращает информацию об отгрузки",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = ShipmentResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:18:03.118+00:00",
                                                         "status": 400,
                                                         "error": "Bad Request",
                                                         "path": "/api/v1/shipment/abcd"
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
                                                        "path": "/api/v1/shipment/10000005"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "403", description = "Доступ к отгрузке запрещен",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:21:10.74Z",
                                                         "status": 403,
                                                         "error": "Access denied",
                                                         "message": "There is no access to this shipment",
                                                         "path": "/api/v1/fulfillment/10000005"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Отгрузка не найдена",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:21:45.21Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Shipment with id: 100000 not found",
                                                         "path": "/api/v1/shipment/100000"
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
                                                        "message": "Couldn't get shipment",
                                                        "path": "/api/v1/shipment/10000005"
                                                    }"""
                                    )))
            })
    @GetMapping("/{shipmentId}")
    @ResponseStatus(HttpStatus.OK)
    ShipmentResponse getShipment(@AuthenticationPrincipal AccountPrincipal account,
                                 @Parameter(
                                         name = "shipmentId",
                                         description = "Id отгрузки",
                                         required = true,
                                         in = ParameterIn.PATH,
                                         schema = @Schema(type = "integer", format = "int64", example = "10006115")
                                 )
                                 @PathVariable long shipmentId);

    @SecurityRequirement(name = "TelegramAuth")
    @Operation(
            summary = "Поиск отгрузок",
            description = "Возвращает список отгрузок пользователя с фильтром и пагинацией в отсортированном по дате виде (сначала новые)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = AllShipmentsResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:34:23.56Z",
                                                         "status": 400,
                                                         "error": "Bad request",
                                                         "message": "The limit cannot exceed 30",
                                                         "path": "/api/v1/shipment"
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
                                                        "path": "/api/v1/shipment"
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
                                                        "message": "Couldn't get shipments",
                                                        "path": "/api/v1/shipment"
                                                    }"""
                                    )))
            })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    AllShipmentsResponse getAllShipments(@AuthenticationPrincipal AccountPrincipal account,
                                         @Parameter(
                                                 name = "search",
                                                 description = "Строка для поиска отгрузок по началу наименования, артикула или баркода товара",
                                                 in = ParameterIn.QUERY,
                                                 example = "костюм"
                                         )
                                         @RequestParam(required = false) String search,

                                         @Parameter(
                                                 name = "status",
                                                 description = "Строка для поиска отгрузок по статусу: Принято, В работе, Отгружено",
                                                 in = ParameterIn.QUERY,
                                                 example = "костюм"
                                         )
                                         @RequestParam(required = false) String status,

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

    @SecurityRequirement(name = "ApiSecretAuth")
    @Operation(
            summary = "Обновление статуса отгрузки",
            description = "Обновляет статус отгрузки. Запрос отправляет гугл таблица",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное обновление",
                            content = @Content(schema = @Schema(implementation = UpdateResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                          "timestamp": "2025-07-28T21:45:09.57Z",
                                                          "message": "Successful status update on: В работе"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T21:46:54.02Z",
                                                         "status": 400,
                                                         "error": "Validation error",
                                                         "message": "Incorrect data was entered",
                                                         "path": "/api/v1/shipment/10000015/status",
                                                         "details": {
                                                             "status": "The status cannot be empty"
                                                         }
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "401", description = "Не аутентифицирован",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T21:49:06.819+00:00",
                                                         "status": 401,
                                                         "error": "Unauthorized",
                                                         "path": "/api/v1/shipment/10000015/status"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Отгрузка для обновления статуса не найдена",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:04:22.00Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Shipment with id: 10000015 not found",
                                                         "path": "/api/v1/shipment/10000015/status"
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
                                                        "message": "Couldn't change the shipment status",
                                                        "path": "/api/v1/shipment/10000015/status"
                                                    }"""
                                    )))
            })
    @PatchMapping("/{shipmentId}/status")
    @ResponseStatus(HttpStatus.OK)
    UpdateResponse changeStatus(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для смены статуса",
                    required = true,
                    content = @Content(mediaType = "application/json"))
            @org.springframework.web.bind.annotation.RequestBody @Valid ChangeShipmentStatusRequest request,

            @Parameter(
                    name = "shipmentId",
                    description = "Id отгрузки",
                    required = true,
                    in = ParameterIn.PATH,
                    schema = @Schema(type = "integer", format = "int64", example = "10006115")
            )
            @PathVariable long shipmentId);

}
