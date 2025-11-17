package org.waterwood.waterfunservice.service.dto;

import cn.hutool.captcha.LineCaptcha;

public record LineCaptchaResult(String uuid, LineCaptcha captcha) {
}
