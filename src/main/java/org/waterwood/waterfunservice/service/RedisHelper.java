package org.waterwood.waterfunservice.service;

import org.springframework.stereotype.Component;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.utils.StringUtil;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class RedisHelper<T> {
    private final RedisRepository<T> redisRepository;
    private String redisKeyPrefix="";

    protected RedisHelper(RedisRepository<T> redisRepository) {
        this.redisRepository = redisRepository;
    }

    public void setRedisKeyPrefix(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix.replace(":", "");
    }

    public void setExpiration(String key, Duration ttl) {
        redisRepository.setExpiration(key, ttl);
    }

    public void saveValue(String key, T value, Duration expire) {
        redisRepository.save(getRedisKey(key), value, expire);
    }

    public void saveMapValue(String key, String field, T value, Duration expire) {
        redisRepository.hashMapSave(getRedisKey(key),field ,value, expire);
    }

    public void addMapValue(String key, String field, T value) {
        redisRepository.hashMapAdd(getRedisKey(key),field,value);
    }

    public void removeMapFields(String key, String... fields) {
        redisRepository.removeField(getRedisKey(key), List.of(fields));
    }

    public T getValue(String key) {
        return redisRepository.get(getRedisKey(key));
    }

    public Map<String,Object> getMapValue(String key) {
        return redisRepository.getAll(key);
    }

    public Set<T> getSetValue(String key) { return redisRepository.getSetValue(redisKeyPrefix+key); }

    public Map<String,Object> getAll(String key) { return redisRepository.getAll(getRedisKey(key));}

    public T getValue(String key, String field) { return (T) redisRepository.get(getRedisKey(key),field); }

    public void removeValue(String key) {
        redisRepository.delete(getRedisKey(key));
    }

    public boolean exists(String key) {
        return redisRepository.exists(getRedisKey(key));
    }

    public boolean fieldExists(String key, String field) {
        return redisRepository.fieldExists(getRedisKey(key) ,field);
    }

    public boolean validate(String key, T value) {
        T stored = getValue(key);
        if (stored == null || !stored.equals(value)) {
            return false;
        }
        removeValue(key);
        return true;
    }

    public Long getExpire(String key) {
        return redisRepository.getExpire(getRedisKey(key));
    }

    public String generateKey() {
        return generateNewUUID();
    }

    /**
     * Build redis key path by {@code delimiter ':'}
     * @param keys redis keys
     * @return joint redis key prefix
     */
    public String buildRedisKey(String... keys) {
        return String.join(":", StringUtil.noNullStringArray(keys));
    }

    public String getRedisKey(String... keys) {
        return redisKeyPrefix.concat(":").concat(String.join(":", keys));
    }

    public String generateNewUUID() {
        return UUID.randomUUID().toString();
    }

}
