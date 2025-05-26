package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.waterwood.waterfunservice.DTO.enums.LoginType;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,property = "loginType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PwdLoginRequestBody.class, name = "PASSWORD"),
        @JsonSubTypes.Type(value = SmsLoginRequestBody.class, name = "SMS"),
        @JsonSubTypes.Type(value = EmailLoginRequestBody.class, name = "EMAIL")
})
public class LoginRequestBody {
    private String username;
    private LoginType loginType;
}
