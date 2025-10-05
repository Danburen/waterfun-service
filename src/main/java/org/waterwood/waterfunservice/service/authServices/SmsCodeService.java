package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.service.dto.SmsCodeResult;
import org.waterwood.waterfunservice.service.SmsService;
import org.waterwood.waterfunservice.service.RedisHelper;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.waterwood.waterfunservice.utils.ValidateUtil.validatePhone;

@Getter
@Service
public class SmsCodeService implements VerifyServiceBase {
    private final RedisHelper<String> redisHelper;
    private static final String SMS_KEY_PREFIX = "verify:sms_code";

    @Value("${expire.sms-code}")
    private Long expireDuration;
    @Value("${aliyun.sms.verify-code.template-name}")
    private String smsCodeTemplate;
    private final SmsService smsService;

    protected SmsCodeService(RedisHelper<String> redisHelper, SmsService smsService) {
        this.redisHelper = redisHelper;
        this.smsService = smsService;
        redisHelper.setRedisKeyPrefix(SMS_KEY_PREFIX);
    }

    public ApiResponse<SmsCodeResult> sendSmsCode(String phoneNumber) {
        if (! validatePhone(phoneNumber)) return ResponseCode.PHONE_NUMBER_EMPTY_OR_INVALID.toApiResponse();
        String code = generateVerifyCode();
        String uuid = UUID.randomUUID().toString();
        SmsCodeResult result = smsService.sendSms(phoneNumber, smsCodeTemplate,
                Map.of("code", code, "time", expireDuration));
        result.setKey(uuid);
        if(result.isSendSuccess()){
            redisHelper.saveValue(redisHelper.buildRedisKey(phoneNumber,uuid),code, Duration.ofMinutes(expireDuration));
        }
        return result.isSendSuccess() ? ApiResponse.success(result) : ApiResponse.failure(result);
    }

    public boolean verifySmsCode(String phoneNumber,String uuid, String code) {
        return redisHelper.validate(redisHelper.buildRedisKey(phoneNumber,uuid), code);
    }

    @Override
    public String generateVerifyCode() {
        return String.valueOf( ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
