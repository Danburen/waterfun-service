package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.DTO.common.EmailTemplateType;
import org.waterwood.waterfunservice.DTO.common.ErrorType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.result.EmailCodeResult;
import org.waterwood.waterfunservice.DTO.common.result.OperationResult;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.service.EmailService;
import org.waterwood.waterfunservice.service.RedisServiceBase;
import org.waterwood.waterfunservice.service.common.ServiceErrorCode;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static org.waterwood.waterfunservice.utils.ValidateUtil.validateEmail;

@Getter
@Service
public class EmailCodeService extends RedisServiceBase<String> implements VerifyServiceBase{
    private static final String redisKeyPrefix = "verify:email-code";
    @Value("${expiration.email-code}")
    private Long expireDuration;
    @Value("${mail.support.email}")
    private String supportEmail;
    private final EmailService emailService;

    protected EmailCodeService(RedisRepository<String> redisRepository, EmailService emailService) {
        super(redisKeyPrefix, redisRepository);
        this.emailService = emailService;
    }

    public OperationResult<EmailCodeResult> sendEmailCode(String emailTo, EmailTemplateType type) {
        if(! validateEmail(emailTo)) {
            return OperationResult.<EmailCodeResult>builder()
                    .errorType(ErrorType.CLIENT)
                    .responseCode(ResponseCode.EMAIL_ADDRESS_EMPTY_OR_INVALID)
                    .build();
        }
        if(emailService == null) {
            return OperationResult.<EmailCodeResult>builder()
                    .errorType(ErrorType.SERVER)
                    .serviceErrorCode(ServiceErrorCode.EMAIL_SERVICE_NOT_AVAILABLE)
                    .build();
        }
        String code = generateVerifyCode();
        String uuid = generateKey();

        Map<String,Object> templateData = new HashMap<>();
        templateData.put("verificationCode",code);
        templateData.put("expireTime",expireDuration);
        templateData.put("supportEmail","support@mail.waterfun.top");

        EmailCodeResult sendResult= emailService.sendHtmlEmail(emailTo, type, templateData);
        sendResult.setKey(uuid);

        if (sendResult.isSendSuccess()){ saveValue(emailTo + "_" + uuid,code, Duration.ofMinutes(expireDuration)); }
        return OperationResult.<EmailCodeResult>builder()
                .trySuccess(true)
                .resultData(sendResult)
                .responseCode(sendResult.isSendSuccess() ? ResponseCode.OK : ResponseCode.INTERNAL_SERVER_ERROR)
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
