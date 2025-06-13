package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.result.SmsCodeResult;
import org.waterwood.waterfunservice.DTO.common.result.SmsCodeSendResult;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.service.RedisServiceBase;
import org.waterwood.waterfunservice.service.SmsService;
import org.waterwood.waterfunservice.service.common.ServiceErrorCode;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Service
public class SmsCodeService extends RedisServiceBase<String> implements VerifyServiceBase {
    private static final String redisKeyPrefix = "verify:sms-code";
    @Value("${expiration.sms-code}")
    private Long expireDuration;
    @Value("${aliyun.sms.verify-code.template-name}")
    private String smsCodeTemplate;
    private final SmsService smsService;

    protected SmsCodeService(RedisRepository<String> redisRepository, SmsService smsService) {
        super(redisKeyPrefix, redisRepository);
        this.smsService = smsService;
    }

    public SmsCodeResult sendSmsCode(String phoneNumber) {
        if (smsService == null) {
            return SmsCodeResult.builder()
                    .trySendSuccess(false)
                    .serviceErrorCode(ServiceErrorCode.SMS_SERVICE_NOT_AVAILABLE)
                    .build();
        }
        String code = generateVerifyCode();
        String uuid = generateNewUUID();
        saveValue(phoneNumber + "_" + uuid, code, Duration.ofMinutes(expireDuration));
        // If smsService is null, return a result indicating failure
        SmsCodeSendResult result = smsService.sendSms(phoneNumber, smsCodeTemplate,
                Map.of("code", code, "time", expireDuration));
        return SmsCodeResult.builder()
                .trySendSuccess(true)
                .result(result)
                .build();
    }

    public boolean verifySmsCode(String phoneNumber,String uuid, String code) {
        return validate(phoneNumber + "_" + uuid, code);
    }

    @Override
    public String generateVerifyCode() {
        return String.valueOf( ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
