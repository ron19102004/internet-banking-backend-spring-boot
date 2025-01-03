package com.ronial.internet_banking.app.services.impls;

import com.ronial.internet_banking.app.dto.user.UserDetailsResponse;
import com.ronial.internet_banking.app.mappers.UserMapper;
import com.ronial.internet_banking.app.services.AuthService;
import com.ronial.internet_banking.common.exceptions.ApplicationException;
import com.ronial.internet_banking.common.security.StringHashSecurity;
import com.ronial.internet_banking.common.utils.RegexUtils;
import com.ronial.internet_banking.common.utils.StringHashAndShortenerUtils;
import com.ronial.internet_banking.domain.entities.future_account.User;
import com.ronial.internet_banking.domain.repositories.UserRepository;
import com.ronial.internet_banking.infrastructure.cache.RedisKeysUtils;
import com.ronial.internet_banking.infrastructure.cache.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public AuthServiceImpl(final UserRepository userRepository, final RedisService redisService, final PasswordEncoder passwordEncoder,
                           final UserMapper userMapper) {
        this.userRepository = userRepository;
        this.redisService = redisService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public UserDetailsResponse register(User user) {
        User userCopy = user;
        try {
            String passwordHash = passwordEncoder.encode(user.getPassword());
            user.setPassword(passwordHash);
            user.setFullName(user.getFullName().toUpperCase());
            User userSaved = userRepository.save(user);
            saveEmailPhoneCCCDIntoRedis(userCopy);
            return userMapper.toUserDetailsResponse(userSaved);
        } catch (Exception e) {
            String message = e.getMessage();
            int indexDuplicateEntry = message.indexOf("Duplicate entry");
            if (indexDuplicateEntry > -1) {
                int indexWordFor = message.indexOf("for");
                String value = message.substring(indexDuplicateEntry, indexWordFor).split("'")[1];
                saveValueExistIntoRedis(value);
                throw new ApplicationException("Duplicate entry: " + value, 400);
            }
            throw new ApplicationException(e.getMessage(), 500);
        }
    }

    private void saveValueExistIntoRedis(String value) {
        String valueHash = StringHashAndShortenerUtils.hashAndShorten(value);
        if (RegexUtils.isEmail(value)) {
            redisService.set(RedisKeysUtils.authEmail(valueHash), 1);
        } else if (RegexUtils.isPhone(value)) {
            redisService.set(RedisKeysUtils.authPhone(valueHash), 1);
        } else {
            redisService.set(RedisKeysUtils.authCCCD(valueHash), 1);
        }
    }

    private void saveEmailPhoneCCCDIntoRedis(User user) {
        String cccdHashRedis = StringHashAndShortenerUtils.hashAndShorten(user.getCccd());
        String emailHashRedis = StringHashAndShortenerUtils.hashAndShorten(user.getEmail());
        String phoneNumberHashRedis = StringHashAndShortenerUtils.hashAndShorten(user.getPhoneNumber());

        redisService.set(RedisKeysUtils.authCCCD(cccdHashRedis), 1);
        redisService.set(RedisKeysUtils.authEmail(emailHashRedis), 1);
        redisService.set(RedisKeysUtils.authPhone(phoneNumberHashRedis), 1);
    }
}
