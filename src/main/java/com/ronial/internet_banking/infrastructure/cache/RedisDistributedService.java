package com.ronial.internet_banking.infrastructure.cache;

public interface RedisDistributedService {
    boolean acquire(String key,String value, long timeLiveSeconds);
    void release(String key);
    void refresh(String key,long timeLiveSeconds);
    void putWithTimeLive(String key,String value, long timeLiveSeconds);
}
