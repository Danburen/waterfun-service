package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.waterwood.waterfunservice.DTO.common.LoginType;

@Data
public class EmailLoginRequestBody {
    @JsonProperty("username")
    private String email;
    private String emailCode;
    private String deviceFp;
    @JsonProperty("loginType")
    private LoginType loginType;
}
