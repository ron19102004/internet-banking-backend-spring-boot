package com.ronial.internet_banking.infrastructure.cache.impl;

import com.ronial.internet_banking.common.exceptions.ApplicationException;
import com.ronial.internet_banking.infrastructure.cache.RedisDistributedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RedisDistributedServiceImpl implements RedisDistributedService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public RedisDistributedServiceImpl(final RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void putWithTimeLive(String key, String value, long timeLiveSeconds) {
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeLiveSeconds));
    }

    @Override
    public boolean acquire(String key, String value, long timeLiveSeconds) {
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, value, Duration.ofSeconds(timeLiveSeconds));
        return success != null && success;
    }

    @Override
    public void release(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void refresh(String key, long timeLiveSeconds) {
        String value = (String) redisTemplate.opsForValue().get(key);
        if (value == null) {
            throw new ApplicationException("Error Redis", 500);
        }
        redisTemplate.opsForValue().set(key, value, Duration.ofSeconds(timeLiveSeconds));
    }
}
