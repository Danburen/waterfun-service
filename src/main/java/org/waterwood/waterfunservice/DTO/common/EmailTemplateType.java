package org.waterwood.waterfunservice.DTO.common;

import lombok.Getter;

@Getter
public enum EmailTemplateType{
    VERIFY_CODE("verify-code","WaterFun 账户验证","WaterFun<verify@mail.waterfun.com>"),
    PASSWORD_RESET("password-reset","WaterFun 密码重置","WaterFun<verify@mail.waterfun.com>"),;
    private final String templateKey;
    private final String subject;
    private final String defaultFrom;
    EmailTemplateType(String templateKey, String subject, String defaultFrom) {
        this.templateKey = templateKey;
        this.subject = subject;
        this.defaultFrom = defaultFrom;
    }
}