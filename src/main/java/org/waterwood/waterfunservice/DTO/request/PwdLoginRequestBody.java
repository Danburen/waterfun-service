package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.waterwood.waterfunservice.DTO.common.LoginType;

@Data
public class PwdLoginRequestBody {
    private String username;
    private String password;
    private String captcha;
    private String deviceFp;
    @JsonProperty("loginType")
    private LoginType loginType;
}

