package com.ronial.internet_banking.infrastructure.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Map;

@Builder
public record JwtPayload(
        String subject,
        Map<String, Object> claims,
        long timeLive
) {
}
