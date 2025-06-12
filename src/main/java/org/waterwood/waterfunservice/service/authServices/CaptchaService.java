package org.waterwood.waterfunservice.service.authServices;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.repository.RedisRepository;
import org.waterwood.waterfunservice.service.RedisServiceBase;

import java.time.Duration;

@Service
@Getter
public class CaptchaService extends RedisServiceBase<String> implements VerifyServiceBase{
    private static final String redisKeyPrefix = "verify:captcha";

    protected CaptchaService(RedisRepository<String> redisRepository) {
        super(redisKeyPrefix,redisRepository);
    }

    public LineCaptchaResult generateCaptcha(){
        LineCaptcha lineCaptcha = generateVerifyCode();
        String uuid = generateKey();
        String code = lineCaptcha.getCode();
        saveValue(uuid,code, Duration.ofMinutes(2));
        return new LineCaptchaResult(uuid,lineCaptcha);
    }

    @Override
    public LineCaptcha generateVerifyCode() {
        return CaptchaUtil.createLineCaptcha(120, 30, 4, 10);
    }

    public record LineCaptchaResult(String uuid, LineCaptcha captcha){}
}
