package ru.itis.fulfillment.api.dto.internal.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Запрос на регистрацию нового пользователя")
public record RegistrationRequest(
        @Schema(description = "Уникальное имя пользователя", example = "Anna")
        @NotBlank(message = "The name cannot be empty")
        @Size(min = 2, max = 30, message = "The name must be between 2 and 30 characters long")
        String name,

        @Schema(description = "Номер телефона пользователя", example = "+79123456789")
        @NotBlank(message = "The phone cannot be empty")
        @Pattern(
                regexp = "^\\+?[1-9][0-9]{7,14}$",
                message = "Invalid phone number format"
        )
        String phone,

        @Schema(description = "Уникальный email пользователя", example = "anna@example.com")
        @NotBlank(message = "The email address cannot be empty")
        @Email(message = "Incorrect email address")
        @Size(max = 255, message = "The email address must not exceed 255 characters")
        String email,

        @Schema(description = "Уникальный api ключ Wildberries", example = "xYjJ9bGci0iJFUzI2NyIs1mtpZCI6I3jIwMjYwNTI2d...")
        @NotBlank(message = "The wb api key cannot be empty")
        @Size(max = 500, message = "The wb Api Key must not exceed 500 characters")
        String wbApiKey
) {}
