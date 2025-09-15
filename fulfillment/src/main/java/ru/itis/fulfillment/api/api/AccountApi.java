package ru.itis.fulfillment.api.api;

import io.swagger.v3.oas.annotations.Operation;
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
import ru.itis.fulfillment.api.dto.internal.request.ChangeKeyRequest;
import ru.itis.fulfillment.api.dto.internal.request.RegistrationRequest;
import ru.itis.fulfillment.api.dto.internal.response.AccountResponse;
import ru.itis.fulfillment.api.dto.internal.response.AppErrorResponse;
import ru.itis.fulfillment.api.dto.internal.response.ChangeKeyResponse;
import ru.itis.fulfillment.api.dto.internal.response.KeyResponse;
import ru.itis.fulfillment.impl.security.account.AccountPrincipal;

@Tag(name = "Account API", description = "API для управления аккаунтом")
@SecurityRequirement(name = "TelegramAuth")
@RequestMapping("/api/v1/account")
public interface AccountApi {

    @Operation(
            summary = "Регистрация нового пользователя",
            description = """
                    Сохраняет нового пользователя и информацию о товарах, полученную от Wildberries.
                    Если api ключ недействителен, пользователь не будет зарегистрирован""",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Успешное сохранение",
                            content = @Content(schema = @Schema(implementation = AccountResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос, в том числе недействительный api ключ Wildberries",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:04:22.00Z",
                                                         "status": 400,
                                                         "error": "Wildberries API Error: unauthorized",
                                                         "message": "token problem; token is malformed: token contains an invalid number of segments",
                                                         "path": "/api/v1/account/register"
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
                                                        "path": "/api/v1/account/register"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "409", description = "Пользователь с таким именем, email, api ключом или телеграммом уже существует",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:16:24.64Z",
                                                        "status": 409,
                                                        "error": "Resource conflict",
                                                        "message": "Account with this wb api key already exists",
                                                        "path": "/api/v1/account/register"
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
                                                        "message": "Couldn't save account",
                                                        "path": "/api/v1/account/register"
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
                                                         "path": "/api/v1/account/register"
                                                    }"""
                                    )))
            })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    AccountResponse register(@RequestHeader("Authorization") String initData,
                             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                     description = "Данные для регистрации",
                                     required = true,
                                     content = @Content(mediaType = "application/json"))
                             @org.springframework.web.bind.annotation.RequestBody @Valid RegistrationRequest registrationRequest);

    @Operation(
            summary = "Получение api ключа пользователя",
            description = "Возвращает api ключ пользователя по telegram id из initData",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = KeyResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован через telegram",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:28:24.68Z",
                                                        "status": 401,
                                                        "error": "Telegram authentication failed",
                                                        "message": "Init data expired. Log in again via Telegram",
                                                        "path": "/api/v1/account/wb-api-key"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Api ключ не найден",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:04:22.00Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Wb api key was not found for this account",
                                                         "path": "/api/v1/account/wb-api-key"
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
                                                        "message": "Couldn't get wb api key",
                                                        "path": "/api/v1/account/wb-api-key"
                                                    }"""
                                    )))
            })
    @GetMapping("/wb-api-key")
    @ResponseStatus(HttpStatus.OK)
    KeyResponse getWbApiKey(@AuthenticationPrincipal AccountPrincipal account);

    @Operation(
            summary = "Обновление api ключа пользователя",
            description = "Обновляет api ключ пользователя. Корректность нового ключа не проверяется",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное обновление",
                            content = @Content(schema = @Schema(implementation = ChangeKeyResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T14:33:51.19Z",
                                                         "status": 400,
                                                         "error": "Validation error",
                                                         "message": "Incorrect data was entered",
                                                         "path": "/api/v1/account/wb-api-key",
                                                         "details": {
                                                             "wbApiKey": "The wb api key cannot be empty"
                                                         }
                                                     }"""
                                    ))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован через telegram",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class),  mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:28:24.68Z",
                                                        "status": 401,
                                                        "error": "Telegram authentication failed",
                                                        "message": "Init data expired. Log in again via Telegram",
                                                        "path": "/api/v1/account/wb-api-key"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Аккаунт для обновления api ключа не найден",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:04:22.00Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Account not found",
                                                         "path": "/api/v1/account/wb-api-key"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "409", description = "Пользователь с таким api ключом уже существует",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T14:30:23.97Z",
                                                         "status": 409,
                                                         "error": "Resource conflict",
                                                         "message": "It is not possible to save this wb api key, it already exists in the database",
                                                         "path": "/api/v1/account/wb-api-key"
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
                                                        "message": "Couldn't change the wb api key",
                                                        "path": "/api/v1/account/wb-api-key"
                                                    }"""
                                    )))
            })
    @PatchMapping("/wb-api-key")
    @ResponseStatus(HttpStatus.OK)
    ChangeKeyResponse changeWbApiKey(@AuthenticationPrincipal AccountPrincipal account,
                                     @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                             description = "Новое значение ключа",
                                             required = true,
                                             content = @Content(mediaType = "application/json"))
                                     @org.springframework.web.bind.annotation.RequestBody @Valid ChangeKeyRequest request);

    @Operation(
            summary = "Получение информации о пользователе",
            description = "Возвращает информацию о пользователе по telegram id из initData",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешно",
                            content = @Content(schema = @Schema(implementation = AccountResponse.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован через telegram",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:28:24.68Z",
                                                        "status": 401,
                                                        "error": "Telegram authentication failed",
                                                        "message": "Init data expired. Log in again via Telegram",
                                                        "path": "/api/v1/account"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:04:22.00Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Account not found",
                                                         "path": "/api/v1/account"
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
                                                        "message": "Couldn't get account data",
                                                        "path": "/api/v1/account"
                                                    }"""
                                    )))
            })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    AccountResponse getAccount(@AuthenticationPrincipal AccountPrincipal account);

    @Operation(
            summary = "Удаление пользователя",
            description = "Безвозвратно удаляет всю информацию, связанную с пользователем, по telegram id из initData",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Успешное удаление",
                            content = @Content(schema = @Schema(implementation = void.class))),
                    @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован через telegram",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                        "timestamp": "2025-07-28T13:28:24.68Z",
                                                        "status": 401,
                                                        "error": "Telegram authentication failed",
                                                        "message": "Init data expired. Log in again via Telegram",
                                                        "path": "/api/v1/account"
                                                    }"""
                                    ))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден",
                            content = @Content(schema = @Schema(implementation = AppErrorResponse.class), mediaType = "application/json",
                                    examples = @ExampleObject(
                                            value = """
                                                    {
                                                         "timestamp": "2025-07-28T13:04:22.00Z",
                                                         "status": 404,
                                                         "error": "No data found",
                                                         "message": "Account to delete was not found",
                                                         "path": "/api/v1/account"
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
                                                        "message": "Couldn't delete account",
                                                        "path": "/api/v1/account"
                                                    }"""
                                    )))
            })
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteAccount(@AuthenticationPrincipal AccountPrincipal account);


}
