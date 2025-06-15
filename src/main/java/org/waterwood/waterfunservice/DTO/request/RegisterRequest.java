package org.waterwood.waterfunservice.DTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class RegisterRequest {
    private String phoneNumber;
    private String username;
    private String smsCode;

    private String password;
    private String email;
}
