package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegisterRequest {
    @JsonProperty("phone")
    private String phoneNumber;
    private String username;
    private String smsCode;

    private String password;
    private String email;
}
