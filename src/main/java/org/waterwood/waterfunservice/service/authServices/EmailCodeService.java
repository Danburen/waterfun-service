package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.service.EmailService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Service
public class EmailCodeService extends VerifyCodeServiceBase{
    private static final String redisKeyPrefix = "verify:email-code";
    @Value("${expiration.email-code}")
    private Long EXPIRE_DURATION;
    private final EmailService emailService;
    protected EmailCodeService(RedisRepository<String> redisRepository,EmailService emailService) {
        super(redisKeyPrefix, redisRepository);
        this.emailService = emailService;
    }

    public boolean sendEmailCode(String emailTo, EmailService.EmailTemplateType type) {
        String code = generateVerifyCode();
        saveCode(emailTo,code, EXPIRE_DURATION);

        Map<String,Object> templateData = new HashMap<>();
        templateData.put("verificationCode",code);
        templateData.put("expireTime",emailService);
        emailService.sendHtmlEmail(emailTo, type, templateData);
        return true;
    }

    @Override
    public String generateVerifyCode() {
        return String.valueOf( ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    public record EmailCodeResult(String uuid,String emailCode){};
}
