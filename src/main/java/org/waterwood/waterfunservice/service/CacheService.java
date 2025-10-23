package org.waterwood.waterfunservice.service;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

public interface CacheService<T> {
    void setKeyPrefix(String redisKeyPrefix);

    void setExpiration(String key, Duration ttl);

    void saveValue(String key, T value, Duration expire);

    void saveMapValue(String key, String field, T value, Duration expire);

    void addMapValue(String key, String field, T value);

    void removeMapFields(String key, String... fields);

    T getValue(String key);

    Map<String,Object> getMapValue(String key);

    Set<T> getSetValue(String key);

    Map<String,Object> getAll(String key);

    T getValue(String key, String field);

    void removeValue(String key);

    boolean exists(String key);

    boolean fieldExists(String key, String field);

    boolean validate(String key, T value);

    Long getExpire(String key);

    String generateKey();

    String buildKeys(String... keys);

    String getCurrentKey(String... keys);

    String generateNewUUID();
}
