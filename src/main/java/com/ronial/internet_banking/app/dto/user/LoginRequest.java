package com.ronial.internet_banking.app.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotNull
        @NotBlank(message = "Phone number is required")
        @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 characters")
        String phoneNumber,
        @NotNull
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password
) {
}
