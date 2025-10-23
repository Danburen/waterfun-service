package org.waterwood.waterfunservice.DTO.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class RegisterRequest {
    @JsonProperty("phone")
    private String phoneNumber;
    private String username;
    private String smsCode;
    private String deviceFp;

    private String password;
    @Email(message = "Invalid email address")
    private String email;
}
