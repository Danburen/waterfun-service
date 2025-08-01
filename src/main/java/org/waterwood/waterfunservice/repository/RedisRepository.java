package org.waterwood.waterfunservice.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class RedisRepository<T> {
    private final RedisTemplate<String,T> redisTemplate;

    private static final String MAP_ADD_AND_SAVE_LUA_SCRIPT = "redis.call('HSET', KEYS[1], ARGV[1], ARGV[2]); " +
            "redis.call('EXPIRE', KEYS[1], ARGV[3]); " +
            "return 1;";
    private static final String SET_ADD_AND_SAVE_LUA_SCRIPT = "redis.call('SADD', KEYS[1], ARGV[1])" +
            "redis.call('EXPIRE', KEYS[1], ARGV[2])" +
            "return 1";

    public RedisRepository(RedisTemplate<String,T> redisTemplate) {
        this.redisTemplate =  redisTemplate;
    }

    public void setExpiration(String key, Duration ttl) {
        redisTemplate.expire(key, ttl);
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
    // String Operations
    public void save(String key, T value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value,ttl);
    }
    public T get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // Hash Operations
    public void hashMapSave(String key, String field, T value, Duration ttl) {
        // atomic hash save and set expiration
        RedisScript<Long> script = new DefaultRedisScript<>(MAP_ADD_AND_SAVE_LUA_SCRIPT, Long.class);
        redisTemplate.execute(script, Collections.singletonList(key),
                field, value, String.valueOf(ttl.toSeconds()));
    }

    public void hashMapAdd(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Object get(String key, String field) { return redisTemplate.opsForHash().get(key,field); }

    public Map<String, Object> getAll(String key){
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        return entries.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        Map.Entry::getValue
                ));
    }

    public List<Object> getFields(String key, List<String> fields) {
        return redisTemplate.opsForHash().multiGet(key, Collections.singleton(fields));
    }

    public void removeField(String key, List<String> fields) {
        redisTemplate.opsForHash().delete(key, fields);
    }

    public boolean fieldExists(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // Set Operations
    public void setSave(String key,T value,Duration ttl) {
        RedisScript<Long> script = new DefaultRedisScript<>(SET_ADD_AND_SAVE_LUA_SCRIPT, Long.class);
        redisTemplate.execute(script, Collections.singletonList(key),
                Collections.singletonList(key),
                value,
                String.valueOf(ttl.toSeconds()));
    }

    public void setAdd(String key,T value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public Set<T> getSetValue(String key) {
        return redisTemplate.opsForSet().members(key);
    }

}
