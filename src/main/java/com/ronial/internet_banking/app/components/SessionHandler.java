package com.ronial.internet_banking.app.components;

import com.ronial.internet_banking.common.exceptions.ApplicationException;
import com.ronial.internet_banking.common.exceptions.AuthException;
import com.ronial.internet_banking.common.exceptions.AuthExceptionMessage;
import com.ronial.internet_banking.domain.entities.future_account.User;
import com.ronial.internet_banking.domain.entities.future_account.UserStatus;
import com.ronial.internet_banking.infrastructure.cache.RedisConstant;
import com.ronial.internet_banking.infrastructure.cache.RedisDistributedService;
import com.ronial.internet_banking.infrastructure.cache.RedisKeysUtils;
import com.ronial.internet_banking.infrastructure.cache.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SessionHandler {
    private final RedisService redisService;
    private final RedisDistributedService redisDistributedService;
    public SessionHandler(RedisService redisService, RedisDistributedService redisDistributedService) {
        this.redisService = redisService;
        this.redisDistributedService = redisDistributedService;
    }
    public void putSession(User user){
        if (user.getStatus() != UserStatus.ACTIVE) {
            String status = user.getStatus().name();
            throw new ApplicationException("Account be " + status, 403);
        }
        Object tokenInRedis = redisService.get(RedisKeysUtils.authToken(user.getId()));
        boolean acquire = redisDistributedService.acquire(RedisKeysUtils.authSession(user.getId()), "1", RedisConstant.SESSION_TIME_LIVE); //15m
        if (tokenInRedis == null && !acquire){
            redisDistributedService.refresh(RedisKeysUtils.authSession(user.getId()), 60 * 15);
        }
        if (tokenInRedis != null && !acquire){
            throw new AuthException(AuthExceptionMessage.SESSION_EXISTS);
        }
    }
    public void popSession(User user){
        redisDistributedService.release(RedisKeysUtils.authSession(user.getId()));
    }
}
