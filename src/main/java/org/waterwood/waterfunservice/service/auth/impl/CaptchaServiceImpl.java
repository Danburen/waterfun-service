package org.waterwood.waterfunservice.service.auth.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import org.springframework.stereotype.Service;
import org.waterwood.waterfunservice.service.auth.CaptchaService;
import org.waterwood.waterfunservice.service.dto.LineCaptchaResult;
import org.waterwood.waterfunservice.infrastructure.cache.RedisHelper;

import java.time.Duration;

@Service
public class CaptchaServiceImpl implements CaptchaService {
    private static final String REDIS_KEY_PREFIX = "verify:captcha";
    private final RedisHelper<String> redisHelper;

    protected CaptchaServiceImpl(RedisHelper<String> redisHelper) {
        this.redisHelper = redisHelper;
        redisHelper.setKeyPrefix(REDIS_KEY_PREFIX);
    }

    @Override
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

    @Override
    public boolean validateCaptcha(String uuid, String code){
        return redisHelper.validate(uuid,code);
    }
}
