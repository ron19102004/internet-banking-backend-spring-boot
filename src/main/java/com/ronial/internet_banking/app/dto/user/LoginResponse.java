package com.ronial.internet_banking.app.dto.user;

public record LoginResponse(
        UserDetailsResponse details,
        String token
) {
}
