package org.waterwood.waterfunservice.service;

import org.waterwood.waterfunservice.repository.RedisRepository;

import java.time.Duration;
import java.util.UUID;


public abstract class RedisServiceBase<T> implements RedisValidateService<T> {
    private final String redisKeyPrefix;
    private final RedisRepository<T> redisRepository;

    protected RedisServiceBase(String redisKeyPrefix, RedisRepository<T> redisRepository) {
        this.redisKeyPrefix = redisKeyPrefix + ":";
        this.redisRepository = redisRepository;
    }

    @Override
    public void saveValue(String key, T value, Duration expire) {
        redisRepository.save(redisKeyPrefix + key, value, expire);
    }

    @Override
    public T getValue(String key) {
        return redisRepository.get(redisKeyPrefix + key);
    }

    @Override
    public void removeValue(String key) {
        redisRepository.delete(redisKeyPrefix + key);
    }

    @Override
    public boolean exists(String key) {
        return redisRepository.exists(redisKeyPrefix + key);
    }

    @Override
    public boolean validate(String key, T value) {
        T stored = getValue(key);
        if (stored == null || !stored.equals(value)) {
            return false;
        }
        removeValue(key);
        return true;
    }

    @Override
    public Long getExpire(String key) {
        return redisRepository.getExpire(redisKeyPrefix + key);
    }

    @Override
    public String generateKey() {
        return generateNewUUID();
    }

    public String buildRawRedisKey(String... keys) {
        return String.join(":", keys);
    }

    public String buildFullRedisKey(String... keys) {
        return redisKeyPrefix + String.join(":", keys);
    }

    public String generateNewUUID() {
        return UUID.randomUUID().toString();
    }
}
