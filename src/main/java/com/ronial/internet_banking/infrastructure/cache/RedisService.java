package com.ronial.internet_banking.infrastructure.cache;

import java.util.List;

public interface RedisService {
    <E> List<E> getList(String key);
    <E> void setList(String key, List<E> ls);
    <E> void set(String key, E e);
    <E> E get(String key);
}
