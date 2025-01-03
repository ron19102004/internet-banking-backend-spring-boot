package com.ronial.internet_banking.infrastructure.security.jwt;

import java.util.Map;

public interface JwtService {
    String generate(JwtPayload payload);
    JwtPayload parse(String token);
}
