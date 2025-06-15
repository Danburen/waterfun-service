package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.ErrorType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.result.OperationResult;
import org.waterwood.waterfunservice.DTO.common.result.SmsCodeResult;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.service.RedisServiceBase;
import org.waterwood.waterfunservice.service.SmsService;
import org.waterwood.waterfunservice.service.common.ServiceErrorCode;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.waterwood.waterfunservice.utils.ValidateUtil.validatePhone;

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

    public OperationResult<SmsCodeResult> sendSmsCode(String phoneNumber) {
        if (! validatePhone(phoneNumber)) {
            return OperationResult.<SmsCodeResult>builder()
                    .errorType(ErrorType.CLIENT)
                    .responseCode(ResponseCode.PHONE_NUMBER_EMPTY_OR_INVALID)
                    .build();
        }
        if (smsService == null) {
            return OperationResult.<SmsCodeResult>builder()
                    .errorType(ErrorType.SERVER)
                    .serviceErrorCode(ServiceErrorCode.SMS_SERVICE_NOT_AVAILABLE)
                    .build();
        }
        String code = generateVerifyCode();
        String uuid = generateNewUUID();
        SmsCodeResult result = smsService.sendSms(phoneNumber, smsCodeTemplate,
                Map.of("code", code, "time", expireDuration));
        result.setKey(uuid);
        if(result.isSendSuccess()){ saveValue(phoneNumber + "_" + uuid, code, Duration.ofMinutes(expireDuration)); }
        return OperationResult.<SmsCodeResult>builder()
                .trySuccess(true)
                .resultData(result)
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
