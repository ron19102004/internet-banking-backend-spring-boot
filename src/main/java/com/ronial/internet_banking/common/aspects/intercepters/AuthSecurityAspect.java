package com.ronial.internet_banking.common.aspects.intercepters;

import com.ronial.internet_banking.common.aspects.annotations.AuthSecurity;
import com.ronial.internet_banking.common.exceptions.AuthException;
import com.ronial.internet_banking.common.exceptions.AuthExceptionMessage;
import com.ronial.internet_banking.domain.entities.future_account.User;
import com.ronial.internet_banking.infrastructure.cache.RedisKeysUtils;
import com.ronial.internet_banking.infrastructure.cache.RedisService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Order(1)
public class AuthSecurityAspect {
    private final RedisService redisService;
    public AuthSecurityAspect(final RedisService redisService) {
        this.redisService = redisService;
    }
    private boolean isAnonymousUser(Object object){
        return object instanceof String;
    }
    @Before("@annotation(authSecurity)")
    public void filter(AuthSecurity authSecurity){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated() || isAnonymousUser(authentication.getPrincipal())) {
            throw new AuthException(AuthExceptionMessage.REQUIRE_AUTHENTICATION);
        }
        User user = (User) authentication.getPrincipal();
        if (authSecurity.checkSession()){
            Object session = redisService.get(RedisKeysUtils.authSession(user.getId()));
            if (session == null){
                throw new AuthException(AuthExceptionMessage.SESSION_EXPIRED);
            }
        }
        if (Arrays.asList(authSecurity.roles()).isEmpty()) return;
        if (!Arrays.asList(authSecurity.roles()).contains(user.getRole())) {
            throw new AuthException(AuthExceptionMessage.FORBIDDEN);
        }

    }
}