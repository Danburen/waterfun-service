package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.waterwood.waterfunservice.DTO.common.LoginType;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "loginType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PwdLoginRequestBody.class, name = "password"),
        @JsonSubTypes.Type(value = SmsLoginRequestBody.class, name = "sms"),
        @JsonSubTypes.Type(value = EmailLoginRequestBody.class, name = "email")
})
public class LoginRequestBody {
    private String username;
    private LoginType loginType;
}
