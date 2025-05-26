package org.waterwood.waterfunservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepository<T> {
    private final RedisTemplate<String,T> redisTemplate;
    public RedisRepository(RedisTemplate<String,T> redisTemplate) {
        this.redisTemplate =  redisTemplate;
    }
    public void save(String key, T value, Duration dur) {
        redisTemplate.opsForValue().set(key, value,dur);
    }
    public T get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
