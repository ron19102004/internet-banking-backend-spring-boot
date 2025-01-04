package com.ronial.internet_banking.common.aspects.intercepters;

import com.ronial.internet_banking.common.aspects.annotations.RateLimiter;
import com.ronial.internet_banking.common.exceptions.RateLimitingException;
import com.ronial.internet_banking.common.utils.StringHashAndShortenerUtils;
import com.ronial.internet_banking.infrastructure.cache.RedisDistributedService;
import com.ronial.internet_banking.infrastructure.cache.RedisService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Aspect
@Order(2)
@Component
public class RateLimiterAspect {
    private final RedisDistributedService redisDistributedService;
    private final RedisService redisService;

    @Autowired
    public RateLimiterAspect(final RedisDistributedService redisDistributedService, final RedisService redisService) {
        this.redisDistributedService = redisDistributedService;
        this.redisService = redisService;
    }

    @Before("@annotation(limiter)")
    public void limiter(RateLimiter limiter) {
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        if (limiter.enableLimitRequest()) limiterRequest(limiter);
        if (limiter.enableLimitIPAdr()) limiterIP(requestAttributes, limiter);
    }

    private void limiterIP(final ServletRequestAttributes requestAttributes, final RateLimiter limiter) {
        final long time = System.nanoTime();
        final String timeShort = StringHashAndShortenerUtils.hashAndShorten(time+"");
        final String ipAdr = requestAttributes.getRequest().getRemoteAddr();
        StringBuilder key = new StringBuilder();
        key
                .append("rate-limiter:ip:")
                .append(limiter.name())
                .append(":")
                .append(ipAdr.replace(":", "_"))
                .append(":");
        final List<String> keysIP = redisService.scanKeys(key + "*");
        if (keysIP.size() >= limiter.limitIPAdr()) {
            throw new RateLimitingException(limiter.overLimitIPMessage());
        }
        redisDistributedService.putWithTimeLive(key.append(timeShort).toString(), "1", limiter.timeLimitIPAdr());
    }

    private void limiterRequest(final RateLimiter limiter) {
        final long time = System.nanoTime();
        final String timeShort = StringHashAndShortenerUtils.hashAndShorten(time+"");
        StringBuilder key = new StringBuilder();
        key
                .append("rate-limiter:req:")
                .append(limiter.name())
                .append(":");
        final List<String> keysIP = redisService.scanKeys(key + "*");
        if (keysIP.size() >= limiter.limitRequest()) {
            throw new RateLimitingException(limiter.overLimitRequestMessage());
        }
        redisDistributedService.putWithTimeLive(key.append(timeShort).toString(), "1", limiter.timeLimitIPAdr());
    }
}
