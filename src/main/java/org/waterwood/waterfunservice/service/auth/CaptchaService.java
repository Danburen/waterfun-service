package org.waterwood.waterfunservice.service.auth;

import cn.hutool.captcha.LineCaptcha;
import org.waterwood.waterfunservice.service.dto.LineCaptchaResult;

public interface CaptchaService extends VerifyServiceBase {
    LineCaptchaResult generateCaptcha();

    @Override
    LineCaptcha generateVerifyCode();

    boolean validateCaptcha(String uuid, String code);
}
