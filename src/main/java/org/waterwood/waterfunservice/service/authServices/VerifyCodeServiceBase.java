package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.repository.RedisRepository;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * A class to handle Verify Code generating and validating
 */
@Getter
@Setter
public abstract class VerifyCodeServiceBase implements VerifyCodeService {
    private final String redisKeyPrefix;
    private final RedisRepository<String> redisRepository;
    protected VerifyCodeServiceBase(String redisKeyPrefix, RedisRepository<String> redisRepository) {
        this.redisKeyPrefix = redisKeyPrefix + ":";
        this.redisRepository = redisRepository;
    }

    @Override
    public void saveCode(String key,String code){
        redisRepository.save(redisKeyPrefix + key, code, Duration.ofMinutes(2));
    }
    @Override
    public void saveCode(String key,String code,long timeout){
        redisRepository.save(redisKeyPrefix  + key, code,Duration.ofMinutes(timeout));
    }
    @Override
    public void saveCode(String key, String code, Duration timeout){
        redisRepository.save(redisKeyPrefix + key, code,timeout);
    }

    @Override
    public String getCode(String key){
        return redisRepository.get(redisKeyPrefix + key);
    }

    @Override
    public void removeCode(String key){
        redisRepository.delete(redisKeyPrefix + key);
    }

    /**
     * Get a new uuid by
     * @see UUID#randomUUID()
     * @return uuid string
     */
    public static String getNewUUID(){
        return UUID.randomUUID().toString();
    }

    /**
     * Validate verify code
     * Compare with the code in redis
     * @param key code key storing at redis
     * @param code code pretend to validate.
     * @return whether the code is right.
     */
    public boolean validateCode(String key, String code){
        String storedCode = getCode(key);
        if (storedCode == null || !storedCode.equals(code)) {
            return false;
        }
        removeCode(key);
        return true;
    }
}
