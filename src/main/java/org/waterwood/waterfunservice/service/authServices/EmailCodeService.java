package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisAccessor;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.result.EmailCodeResult;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.service.EmailService;
import org.waterwood.waterfunservice.service.RedisServiceBase;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Service
public class EmailCodeService extends RedisServiceBase<String> implements VerifyServiceBase{
    private static final String redisKeyPrefix = "verify:email-code";
    @Value("${expiration.email-code}")
    private Long expireDuration;
    private final EmailService emailService;

    protected EmailCodeService(RedisRepository<String> redisRepository,EmailService emailService) {
        super(redisKeyPrefix, redisRepository);
        this.emailService = emailService;
    }

    public EmailCodeResult sendEmailCode(String emailTo, EmailService.EmailTemplateType type) {
        if(emailService == null) {
            return EmailCodeResult.builder()
                    .trySendSuccess(false)
                    .authErrorCode(AuthErrorCode.EMAIL_SERVICE_NOT_AVAILABLE)
                    .build();
        }
        String code = generateVerifyCode();
        String uuid = generateKey();
        saveValue(emailTo + "_" + uuid,code, Duration.ofMinutes(expireDuration));

        Map<String,Object> templateData = new HashMap<>();
        templateData.put("verificationCode",code);
        templateData.put("expireTime",expireDuration);
        return EmailCodeResult.builder()
                .trySendSuccess(true)
                .result(emailService.sendHtmlEmail(emailTo, type, templateData))
                .build();
    }

    public boolean verifyEmailCode(String email,String uuid, String code) {
        return validate(email + "_" + uuid, code);
    }

    @Override
    public String generateVerifyCode() {
        return String.valueOf( ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

}
