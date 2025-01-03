package com.ronial.internet_banking.infrastructure.security.jwt;

import com.ronial.internet_banking.domain.entities.future_account.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JwtConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {
    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        User user = User.builder()
                .id(Long.parseLong(jwt.getSubject()))
                .build();
        return new UsernamePasswordAuthenticationToken(user, jwt, List.of());
    }
}
