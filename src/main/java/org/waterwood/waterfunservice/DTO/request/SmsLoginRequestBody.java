package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.waterwood.waterfunservice.DTO.common.LoginType;

@Data
public class SmsLoginRequestBody {
    @JsonProperty("username")
    private String phoneNumber;
    private String smsCode;
    @JsonProperty("loginType")
    private LoginType loginType;
}
