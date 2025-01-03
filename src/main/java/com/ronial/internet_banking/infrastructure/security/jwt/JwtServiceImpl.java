package com.ronial.internet_banking.infrastructure.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtServiceImpl implements JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Autowired
    public JwtServiceImpl(final JwtEncoder jwtEncoder, final JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    /**
     * Payload.timeLive: minutes
     */
    @Override
    public String generate(JwtPayload payload) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claimsSetBuilder = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(now.plus(payload.timeLive(), ChronoUnit.MINUTES))
                .subject(payload.subject());
        payload.claims().forEach(claimsSetBuilder::claim);
        JwtClaimsSet claimsSet = claimsSetBuilder.build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    @Override
    public JwtPayload parse(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return new JwtPayload(jwt.getSubject(), jwt.getClaims(), 0L);
    }
}
