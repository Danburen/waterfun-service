package org.waterwood.waterfunservice.service;

import java.time.Duration;

public interface RedisValidateService<T>{
    void saveValue(String key, T value, Duration expire);
    T getValue(String key);
    void removeValue(String key);
    boolean exists(String key);
    boolean validate(String key, T value);
    Long getExpire(String key);
    String generateKey();
}
