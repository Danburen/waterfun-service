package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.EmailTemplateType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.service.dto.EmailCodeResult;
import org.waterwood.waterfunservice.service.Impl.ResendEmailService;
import org.waterwood.waterfunservice.service.RedisHelper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.waterwood.waterfunservice.utils.ValidateUtil.validateEmail;

@Getter
@Service
public class EmailCodeService implements VerifyServiceBase{
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
        redisHelper.setRedisKeyPrefix(REDIS_KEY_PREFIX);
    }

    public ApiResponse<EmailCodeResult> sendEmailCode(String emailTo, EmailTemplateType type) {
        if(! validateEmail(emailTo)) {
            return ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID.toApiResponse();
        }
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
            return ApiResponse.success(sendResult);
        }else{
            return ApiResponse.failure(sendResult);
        }
    }

    public boolean verifyEmailCode(String email,String uuid, String code) {
        return redisHelper.validate(email + "_" + uuid, code);
    }

    @Override
    public String generateVerifyCode() {
        return String.valueOf( ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
