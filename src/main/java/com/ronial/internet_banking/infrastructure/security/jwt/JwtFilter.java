package com.ronial.internet_banking.infrastructure.security.jwt;

import com.ronial.internet_banking.app.services.UserService;
import com.ronial.internet_banking.common.exceptions.AuthException;
import com.ronial.internet_banking.common.exceptions.AuthExceptionMessage;
import com.ronial.internet_banking.domain.entities.future_account.User;
import com.ronial.internet_banking.infrastructure.cache.RedisKeysUtils;
import com.ronial.internet_banking.infrastructure.cache.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final RedisService redisService;

    @Autowired
    public JwtFilter(final JwtService jwtService, final UserService userService, final RedisService redisService) {
        this.jwtService = jwtService;
        this.userService = userService;
        this.redisService = redisService;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String jwtToken = authorizationHeader.substring(7);
            JwtPayload jwtPayload = jwtService.parse(jwtToken);
            //Check token in redis
            Object tokenInRedis = redisService.get(RedisKeysUtils.authToken(Long.parseLong(jwtPayload.subject())));
            if (tokenInRedis == null) {
                throw new AuthException(AuthExceptionMessage.TOKEN_NOT_EXISTS);
            }
            if (!tokenInRedis.equals(jwtToken)) {
                throw new AuthException(AuthExceptionMessage.TOKEN_INCORRECT);
            }
            User user = userService.findById(Long.parseLong(jwtPayload.subject()));

            if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null,
                        user.getAuthorities()
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
        } finally {
            filterChain.doFilter(request, response);
        }
    }
}
