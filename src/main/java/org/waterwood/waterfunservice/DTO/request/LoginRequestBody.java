package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.waterwood.waterfunservice.DTO.common.LoginType;

@Data
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "loginType")
public class LoginRequestBody {
    private LoginType loginType;
    private String accessToken;
    private String refreshToken;
}
