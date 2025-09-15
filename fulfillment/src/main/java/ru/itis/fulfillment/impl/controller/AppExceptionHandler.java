package ru.itis.fulfillment.impl.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.itis.fulfillment.api.dto.external.response.WbErrorResponse;
import ru.itis.fulfillment.api.dto.internal.response.AppErrorResponse;
import ru.itis.fulfillment.impl.exception.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class AppExceptionHandler {

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public AppErrorResponse handleAccountExistsException(ConflictException e, HttpServletRequest request) {
        return new AppErrorResponse(HttpStatus.CONFLICT,
                "Resource conflict",
                e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public AppErrorResponse handleForbiddenException(ForbiddenException e, HttpServletRequest request) {
        return new AppErrorResponse(HttpStatus.FORBIDDEN,
                "Access denied",
                e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(AuthenticationServiceException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public AppErrorResponse handleAuthenticationException(AuthenticationServiceException e, HttpServletRequest request) {
        return new AppErrorResponse(HttpStatus.UNAUTHORIZED,
                "Telegram authentication failed",
                e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.warn("Validation error: {}", e.getMessage());
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String field = error.getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        return new AppErrorResponse(HttpStatus.BAD_REQUEST,
                "Validation error",
                "Incorrect data was entered",
                request.getServletPath(),
                errors);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public AppErrorResponse handleBadRequestException(BadRequestException e, HttpServletRequest request) {
        return new AppErrorResponse(HttpStatus.BAD_REQUEST,
                "Bad request",
                e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public AppErrorResponse handleDatabaseException(DatabaseException e, HttpServletRequest request) {
        return new AppErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Database error",
                e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppErrorResponse handleNotFoundException(NotFoundException e, HttpServletRequest request) {
        log.warn(e.getMessage());
        return new AppErrorResponse(HttpStatus.NOT_FOUND,
                "No data found",
                e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public AppErrorResponse handleNoHandlerFound(NoHandlerFoundException e, HttpServletRequest request) {
        return new AppErrorResponse(HttpStatus.NOT_FOUND,
                "Not found",
                e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public AppErrorResponse handleMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        return new AppErrorResponse(HttpStatus.METHOD_NOT_ALLOWED,
                "Method not allowed",
                e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(GoogleSheetsException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public AppErrorResponse handleGoogleSheetsException(GoogleSheetsException e, HttpServletRequest request) {
        return new AppErrorResponse(HttpStatus.BAD_GATEWAY,
                "Google Sheets API error",
                e.getUserMessage() != null ? e.getUserMessage() : e.getMessage(),
                request.getServletPath());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<AppErrorResponse> handleJsonParseException(HttpMessageNotReadableException e, HttpServletRequest request) {
        log.warn("Client sent invalid JSON: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new AppErrorResponse(HttpStatus.BAD_REQUEST,
                        "Invalid request format",
                        "Invalid JSON format in request body",
                        request.getServletPath()
                ));
    }

    @ExceptionHandler(WildberriesApiException.class)
    public ResponseEntity<AppErrorResponse> handleWildberriesApiException(WildberriesApiException e, HttpServletRequest request) {
        try {
            HttpStatusCodeException httpEx = (HttpStatusCodeException) e.getCause();
            HttpStatusCode statusCode;
            if (httpEx.getStatusCode().value() == 401){
                statusCode = HttpStatusCode.valueOf(400);
            } else if (httpEx.getStatusCode().value() == 403 || httpEx.getStatusCode().value() == 429){
                statusCode = httpEx.getStatusCode();
            } else {
                statusCode = HttpStatusCode.valueOf(502);
            }
            if (!httpEx.getResponseBodyAsString().isEmpty()) {
                WbErrorResponse wbError = objectMapper.readValue(
                        httpEx.getResponseBodyAsString(),
                        WbErrorResponse.class
                );
                AppErrorResponse errorResponse = convertWbErrorToAppError(statusCode, wbError, request.getServletPath());
                return ResponseEntity.status(statusCode).body(errorResponse);
            } else {
                return ResponseEntity.status(statusCode).body(
                        new AppErrorResponse(statusCode.value(),
                                "Wildberries API Error",
                                ((HttpStatus) statusCode).getReasonPhrase(),
                                request.getServletPath()));
            }
        } catch (JsonProcessingException ex) {
            log.error("Error parsing Wildberries response data: {}", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new AppErrorResponse(HttpStatus.BAD_GATEWAY,
                            "Wildberries API Error",
                            "Error parsing Wildberries response data",
                            request.getServletPath()));
        }
    }

    private AppErrorResponse convertWbErrorToAppError(HttpStatusCode statusCode, WbErrorResponse wbError, String path) {
        String errorTitle = wbError.getTitle() != null ? "Wildberries API Error: " + wbError.getTitle() : "Wildberries API Error";
        String errorMessage = wbError.getDetail() != null ? wbError.getDetail() :
                (wbError.getMessage() != null ? wbError.getMessage() :
                        (wbError.getErrorText() != null ? wbError.getErrorText() : ((HttpStatus) statusCode).getReasonPhrase()));

        return new AppErrorResponse(
                wbError.getTimestamp() != null ?
                        OffsetDateTime.parse(wbError.getTimestamp()) :
                        OffsetDateTime.now(ZoneOffset.UTC),
                statusCode.value(),
                errorTitle,
                errorMessage,
                path
        );
    }
}
