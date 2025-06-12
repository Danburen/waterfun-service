package org.waterwood.waterfunservice.DTO.request;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.waterwood.waterfunservice.DTO.common.LoginType;

@Data
@EqualsAndHashCode(callSuper = true)
public class SmsLoginRequestBody extends LoginRequestBody {
    private String smsCode;
    public SmsLoginRequestBody() {
        this.setLoginType(LoginType.SMS);
    }
}
