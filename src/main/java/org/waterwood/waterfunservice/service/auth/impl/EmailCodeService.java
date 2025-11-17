package org.waterwood.waterfunservice.service.auth.impl;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.dto.response.auth.EmailCodeResult;
import org.waterwood.waterfunservice.dto.common.enums.EmailTemplateType;
import org.waterwood.waterfunservice.infrastructure.exception.service.ServiceException;
import org.waterwood.waterfunservice.service.auth.VerifyServiceBase;
import org.waterwood.waterfunservice.service.email.ResendEmailService;
import org.waterwood.waterfunservice.infrastructure.cache.RedisHelper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Service
public class EmailCodeService implements VerifyServiceBase {
    private static final String REDIS_KEY_PREFIX = "verify:email-code";
    private final RedisHelper<String> redisHelper;
    @Value("${expire.email-code}")
    private Long expireDuration;
    @Value("${mail.support.email}")
    private String supportEmail;
    private final ResendEmailService emailService;

    protected EmailCodeService(RedisHelper<String> redisHelper, ResendEmailService emailService) {
        this.redisHelper = redisHelper;
        this.emailService = emailService;
        redisHelper.setKeyPrefix(REDIS_KEY_PREFIX);
    }

    public EmailCodeResult sendEmailCode(String emailTo, EmailTemplateType type) {
        String code = generateVerifyCode();
        String uuid = redisHelper.generateKey();

        Map<String,Object> templateData = new HashMap<>();
        templateData.put("verificationCode",code);
        templateData.put("expireTime",expireDuration);
        templateData.put("supportEmail","support@mail.waterfun.top");

        EmailCodeResult sendResult= emailService.sendHtmlEmail(emailTo, type, templateData);
        sendResult.setKey(uuid);
        if (sendResult.isSendSuccess()){
            redisHelper.saveValue(emailTo + "_" + uuid,code, Duration.ofMinutes(expireDuration));
        }else{
            throw new ServiceException("Email Send Failed" + sendResult.getResponseRaw());
        }
        return sendResult;
    }

    public boolean verifyEmailCode(String email,String uuid, String code) {
        return redisHelper.validate(email + "_" + uuid, code);
    }

    @Override
    public String generateVerifyCode() {
        return String.valueOf( ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
