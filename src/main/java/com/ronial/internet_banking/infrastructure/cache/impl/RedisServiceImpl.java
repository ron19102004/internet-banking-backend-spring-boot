package com.ronial.internet_banking.infrastructure.cache.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronial.internet_banking.infrastructure.cache.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisServiceImpl(final RedisTemplate<String, Object> redisTemplate, final ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <E> List<E> getList(String key) {
        String json = (String) redisTemplate.opsForValue().get(key);
        if (json != null) {
            try {
                return objectMapper.readValue(json, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to deserialize list", e);
            }
        }
        return null;
    }

    @Override
    public <E> void setList(String key, List<E> ls) {
        try {
            String json = objectMapper.writeValueAsString(ls);
            redisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize list", e);
        }
    }

    @Override
    public <E> void set(String key, E e) {
       redisTemplate.opsForValue().set(key, e);
    }

    @Override
    public <E> E get(String key) {
      try {
          Object o = redisTemplate.opsForValue().get(key);
          return o == null ? null : (E) o;
      } catch (Exception e) {
          throw new RuntimeException("Failed to deserialize object", e);
      }
    }
    @Override
    public List<String> scanKeys(String pattern) {
        List<String> keys = new ArrayList<>();
        redisTemplate.execute((RedisConnection connection) -> {
            Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions()
                            .match(pattern) // Đặt pattern, ví dụ: "redis:ip:*"
                            .count(100)    // Gợi ý số lượng key trong mỗi lần quét
                            .build()
            );
            cursor.forEachRemaining(key -> keys.add(new String(key)));
            return null; // Không cần trả về kết quả từ execute
        });
        return keys;
    }
}
