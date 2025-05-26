package org.waterwood.waterfunservice.DTO.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.waterwood.waterfunservice.DTO.enums.LoginType;

@Data
@EqualsAndHashCode(callSuper = true)
public class PwdLoginRequestBody extends LoginRequestBody {
    private String password;
    private String captcha;

    public PwdLoginRequestBody() {
        this.setLoginType(LoginType.PASSWORD);
    }
}

