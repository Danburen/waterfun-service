package org.waterwood.waterfunservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testSet() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("username", "hachett", Duration.ofSeconds(30L));
    }
    @Test
    public void testGet() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        String value = ops.get("username");
        System.out.println(value);

    }
}
