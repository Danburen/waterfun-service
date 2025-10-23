package org.waterwood.waterfunservice.service.authServices;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.service.Impl.RedisHelper;

import java.time.Duration;

@Service
@Getter
public class CaptchaService implements VerifyServiceBase{
    private static final String REDIS_KEY_PREFIX = "verify:captcha";
    private final RedisHelper<String> redisHelper;

    protected CaptchaService(RedisHelper<String> redisHelper) {
        this.redisHelper = redisHelper;
        redisHelper.setKeyPrefix(REDIS_KEY_PREFIX);
    }

    public LineCaptchaResult generateCaptcha(){
        LineCaptcha lineCaptcha = generateVerifyCode();
        String uuid = redisHelper.generateKey();
        String code = lineCaptcha.getCode();
        redisHelper.saveValue(uuid,code, Duration.ofMinutes(2));
        return new LineCaptchaResult(uuid,lineCaptcha);
    }

    @Override
    public LineCaptcha generateVerifyCode() {
        return CaptchaUtil.createLineCaptcha(120, 30, 4, 10);
    }

    public boolean validateCaptcha(String uuid, String code){
        return redisHelper.validate(uuid,code);
    }


    public record LineCaptchaResult(String uuid, LineCaptcha captcha){}
}
