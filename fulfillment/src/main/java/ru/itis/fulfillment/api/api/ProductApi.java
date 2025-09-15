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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.itis.fulfillment.api.dto.internal.response.*;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;

@Tag(name = "Product API", description = "API для управления товарами")
@RequestMapping("/api/v1/product")
@SecurityRequirement(name = "TelegramAuth")
public interface ProductApi {

    @Operation(
            summary = "Поиск товаров",
            description = "Возвращает список товаров пользователя с фильтром и пагинацией",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = AllProductsResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T19:34:23.56Z",
                                                         "status": 400,
                                                         "error": "Bad request",
                                                         "message": "The limit cannot exceed 30",
                                                         "path": "/api/v1/product"
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
                                                        "path": "/api/v1/product"
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
                                                        "message": "Couldn't get products",
                                                        "path": "/api/v1/product"
                                                    }"""
                                    )))
            })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    AllProductsResponse getAllProducts(@AuthenticationPrincipal AccountPrincipal account,
                                       @Parameter(
                                               name = "search",
                                               description = "Строка для поиска товаров по началу наименования, артикула или баркода",
                                               in = ParameterIn.QUERY,
                                               example = "кюлоты"
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
                                       @RequestParam(defaultValue = "30") int limit);

    @Operation(
            summary = "Обновление информации о товарах",
            description = "Обновляет информацию о товарах пользователя в базе данных, делая запрос к api Wildberries",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = UpdateResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Недействительный api ключ Wildberries",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:04:22.00Z",
                                                         "status": 400,
                                                         "error": "Wildberries API Error: unauthorized",
                                                         "message": "token problem; token is malformed: token contains an invalid number of segments",
                                                         "path": "/api/v1/product"
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
                                                        "path": "/api/v1/product"
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
                                                        "message": "Couldn't update product information",
                                                        "path": "/api/v1/product"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "502", description = "Ошибка при работе с api Wildberries",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:22:31.00Z",
                                                         "status": 502,
                                                         "error": "Wildberries API Error: method not allowed",
                                                         "message": "allowed methods are listed in the Allow header (POST)",
                                                         "path": "/api/v1/product"
                                                    }"""
                                    )))
            })
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    UpdateResponse updateProductInformation(@AuthenticationPrincipal AccountPrincipal account);

    @Operation(
            summary = "Получение товара",
            description = "Возвращает информацию о товаре пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = AllProductsResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T20:34:31.029+00:00",
                                                         "status": 400,
                                                         "error": "Bad Request",
                                                         "path": "/api/v1/product/abcd"
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
                                                        "path": "/api/v1/product/10000420"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "403", description = "Доступ к товару запрещен",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T20:36:51.87Z",
                                                         "status": 403,
                                                         "error": "Access denied",
                                                         "message": "There is no access to this product",
                                                         "path": "/api/v1/product/10016160"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Товар не найден",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T20:36:07.95Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Product with id: 1000000 not found",
                                                         "path": "/api/v1/product/1000000"
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
                                                        "message": "Couldn't get product",
                                                        "path": "/api/v1/product/10000005"
                                                    }"""
                                    )))
            })
    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    ProductResponse getProduct(@AuthenticationPrincipal AccountPrincipal account,
                               @Parameter(
                                       name = "productId",
                                       description = "Id товара",
                                       required = true,
                                       in = ParameterIn.PATH,
                                       schema = @Schema(type = "integer", format = "int64", example = "10000420")
                               )
                               @PathVariable long productId);
}
