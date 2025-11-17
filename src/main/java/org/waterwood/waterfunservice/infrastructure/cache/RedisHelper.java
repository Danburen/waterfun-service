package org.waterwood.waterfunservice.infrastructure.cache;

import org.springframework.stereotype.Component;
import org.waterwood.waterfunservice.infrastructure.utils.StringUtil;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class RedisHelper<T> implements CacheService<T> {
    private final RedisRepository<T> redisRepository;
    private String redisKeyPrefix="";

    protected RedisHelper(RedisRepository<T> redisRepository) {
        this.redisRepository = redisRepository;
    }

    @Override
    public void setKeyPrefix(String redisKeyPrefix) {
        this.redisKeyPrefix = redisKeyPrefix.replace(":", "");
    }

    @Override
    public void setExpiration(String key, Duration ttl) {
        redisRepository.setExpiration(key, ttl);
    }

    @Override
    public void saveValue(String key, T value, Duration expire) {
        redisRepository.save(getCurrentKey(key), value, expire);
    }

    @Override
    public void saveMapValue(String key, String field, T value, Duration expire) {
        redisRepository.hashMapSave(getCurrentKey(key),field ,value, expire);
    }

    @Override
    public void addMapValue(String key, String field, T value) {
        redisRepository.hashMapAdd(getCurrentKey(key),field,value);
    }

    @Override
    public void removeMapFields(String key, String... fields) {
        redisRepository.removeField(getCurrentKey(key), List.of(fields));
    }

    @Override
    public T getValue(String key) {
        return redisRepository.get(getCurrentKey(key));
    }

    @Override
    public Map<String,Object> getMapValue(String key) {
        return redisRepository.getAll(key);
    }

    @Override
    public Set<T> getSetValue(String key) { return redisRepository.getSetValue(redisKeyPrefix+key); }

    @Override
    public Map<String,Object> getAll(String key) { return redisRepository.getAll(getCurrentKey(key));}

    @Override
    public T getValue(String key, String field) { return (T) redisRepository.get(getCurrentKey(key),field); }

    @Override
    public void removeValue(String key) {
        redisRepository.delete(getCurrentKey(key));
    }

    @Override
    public boolean exists(String key) {
        return redisRepository.exists(getCurrentKey(key));
    }

    @Override
    public boolean fieldExists(String key, String field) {
        return redisRepository.fieldExists(getCurrentKey(key) ,field);
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
        return redisRepository.getExpire(getCurrentKey(key));
    }

    @Override
    public String generateKey() {
        return generateNewUUID();
    }

    /**
     * Build redis key path by {@code delimiter ':'}
     * @param keys redis keys
     * @return joint redis key prefix
     */
    @Override
    public String buildKeys(String... keys) {
        return String.join(":", StringUtil.noNullStringArray(keys));
    }

    @Override
    public String getCurrentKey(String... keys) {
        if(StringUtil.isBlank(redisKeyPrefix)){
            return String.join(":", keys);
        }
        return redisKeyPrefix.concat(":").concat(String.join(":", keys));
    }

    @Override
    public String generateNewUUID() {
        return UUID.randomUUID().toString();
    }

}
