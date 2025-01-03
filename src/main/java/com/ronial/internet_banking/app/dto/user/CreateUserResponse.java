package com.ronial.internet_banking.app.dto.user;

public record CreateUserResponse(
        UserDetailsResponse details,
        String token
) {
}
