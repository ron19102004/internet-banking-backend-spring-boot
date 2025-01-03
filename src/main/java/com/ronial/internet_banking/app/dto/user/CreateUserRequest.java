package com.ronial.internet_banking.app.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


public record CreateUserRequest(
        @NotNull @NotBlank(message = "Full name is required") String fullName,
        @NotNull @NotBlank(message = "CCCD is required") String cccd,
        @NotNull @NotBlank @Email(message = "Invalid email format") String email,
        @NotNull @NotBlank @Size(min = 10, max = 11, message = "Phone number must be between 10 and 11 characters") String phoneNumber,
        @NotNull @NotBlank @Size(min = 8, message = "Password must be at least 8 characters long") String password,
        @NotNull @NotBlank(message = "Address is required") String address
) {
}