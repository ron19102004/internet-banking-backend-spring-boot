package com.ronial.internet_banking.app.validations;

import com.ronial.internet_banking.common.exceptions.ValidationException;
import com.ronial.internet_banking.common.security.StringHashSecurity;
import com.ronial.internet_banking.common.utils.StringHashAndShortenerUtils;
import com.ronial.internet_banking.domain.entities.future_account.User;
import com.ronial.internet_banking.infrastructure.cache.RedisKeysUtils;
import com.ronial.internet_banking.infrastructure.cache.RedisService;

import org.springframework.stereotype.Component;

@Component
public class AuthValidation {
    private final RedisService redisService;

    public AuthValidation(final RedisService redisService) {
        this.redisService = redisService;
    }

    public void register(User user) {
        //Check cccd in redis
        String cccdHashRedis = StringHashAndShortenerUtils.hashAndShorten(user.getCccd());
        Object oCCCD = redisService.get(RedisKeysUtils.authCCCD(cccdHashRedis));
        if (oCCCD != null) {
            throw new ValidationException("CCCD already in use", 400);
        }
        //Check email in redis
        String emailHashRedis = StringHashAndShortenerUtils.hashAndShorten(user.getEmail());
        Object oEmail = redisService.get(RedisKeysUtils.authEmail(emailHashRedis));
        if (oEmail != null) {
            throw new ValidationException("Email already in use", 400);
        }
        //Check phone number in redis
        String phoneNumberHashRedis = StringHashAndShortenerUtils.hashAndShorten(user.getPhoneNumber());
        Object oPhone = redisService.get(RedisKeysUtils.authPhone(phoneNumberHashRedis));
        if (oPhone != null) {
            throw new ValidationException("Phone number already in use", 400);
        }
    }
}
