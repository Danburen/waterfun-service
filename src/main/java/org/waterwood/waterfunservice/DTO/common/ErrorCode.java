package org.waterwood.waterfunservice.DTO.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.waterwood.waterfunservice.DTO.common.response.LoginResponse;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USERNAME_EMPTY(40001, "USERNAME_EMPTY"),
    PASSWORD_EMPTY(40002, "PASSWORD_EMPTY"),
    USERNAME_OR_PASSWORD_INCORRECT(40003, "USERNAME_OR_PASSWORD_INCORRECT"),
    CAPTCHA_EXPIRED(40004,"CAPTCHA_EXPIRED"),
    CAPTCHA_INCORRECT(40005, "CAPTCHA_INCORRECT"),
    VERIFY_CODE_EXPIRED(40006, "VERIFY_CODE_EXPIRED"),
    VERIFY_CODE_INCORRECT(40007, "VERIFY_CODE_INCORRECT"),
    SMS_CODE_EXPIRED(40008, "SMS_CODE_EXPIRED"),
    SMS_CODE_INCORRECT(40009, "SMS_CODE_INCORRECT"),
    EMAIL_CODE_EXPIRED(40010, "EMAIL_CODE_EXPIRED"),
    EMAIL_CODE_INCORRECT(40011, "EMAIL_CODE_INCORRECT"),
    CAPTCHA_EMPTY(nextCode(), "CAPTCHA_EMPTY"),
    SMS_CODE_EMPTY(nextCode(), "SMS_CODE_EMPTY"),
    EMAIL_CODE_EMPTY(nextCode(), "EMAIL_CODE_EMPTY");

    private static int BASE_CODE = 40012; // Starting code for auto-generated values

    private final int code;    // error code
    private final String msg; // error message

    private static int nextCode() {
        return BASE_CODE++;
    }

    public ResponseEntity<?> toResponseEntity() {
        Map<String,Object> body = new HashMap<>();
        body.put("code", this.code);
        body.put("message", this.msg);
        return ResponseEntity.badRequest().body(body);
    }

    public LoginResponse.LoginResponseBuilder toLoginResponseBuilder(LoginResponse loginResponse) {
        return LoginResponse.builder()
                .success(loginResponse.isSuccess())
                .code(this.code)
                .message(this.msg)
                .accessToken(loginResponse.getAccessToken())
                .userId(loginResponse.getUserId())
                .username(loginResponse.getUsername());
    }

    public LoginResponse.LoginResponseBuilder toLoginResponseBuilder() {
        return LoginResponse.builder()
                .success(false)
                .code(this.code)
                .message(this.msg);
    }

    public LoginResponse toLoginResponse(LoginResponse response) {
        return toLoginResponseBuilder(response).build();
    }

}
