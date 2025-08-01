package org.waterwood.waterfunservice.service;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class WhiteListService {
    private static final String REDIS_TOKEN_KEY_PREFIX = "whitelist";
    private final RedisHelper<String> redisHelper;

    protected WhiteListService(RedisHelper<String> redisHelper) {
        this.redisHelper = redisHelper;
        redisHelper.setRedisKeyPrefix(REDIS_TOKEN_KEY_PREFIX);
    }

    public void add(String key, String value, Duration duration) {
        redisHelper.saveValue(key,value,duration);
    }
    public String get(String key) { return redisHelper.getValue(key);  }

    public void addMapValue(String keyPrefix,String hKey,String hField,String value,Duration dur) {
        redisHelper.saveMapValue(redisHelper.buildRedisKey(keyPrefix, hKey), hField,value,dur);
    }

    public String getMapValue(String keyPrefix,String hKey,String hField) {
        return redisHelper.getValue(redisHelper.buildRedisKey(keyPrefix, hKey), hField);
    }
}
