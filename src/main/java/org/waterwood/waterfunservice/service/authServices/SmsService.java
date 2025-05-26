package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.repository.RedisRepository;

import java.util.concurrent.ThreadLocalRandom;

@Getter
@Service
public class SmsService extends VerifyCodeServiceBase{
    private static final String redisKeyPrefix = "verify:sms-code";
    protected SmsService(RedisRepository<String> redisRepository) {
        super(redisKeyPrefix, redisRepository);
    }

    @Override
    public SmsCodeResult generateVerifyCode() {
        String code = String.valueOf( ThreadLocalRandom.current().nextInt(100000, 1000000));
        String uuid = getNewUUID();
        saveCode(uuid,code);
        return new SmsCodeResult(uuid,code);
    }

    public record SmsCodeResult(String code, String uuid) {}
}
