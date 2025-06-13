package org.waterwood.waterfunservice.DTO.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200, "SUCCESS"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR"),
    UNKNOWN_ERROR(50000, "UNKNOWN_ERROR"),

    // User-info-related Errors
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
    CAPTCHA_EMPTY(40012, "CAPTCHA_EMPTY"),
    SMS_CODE_EMPTY(40013, "SMS_CODE_EMPTY"),
    EMAIL_CODE_EMPTY(40014, "EMAIL_CODE_EMPTY"),

    // Authentication Errors
    ACCESS_TOKEN_EXPIRED(40101, "ACCESS_TOKEN_EXPIRED"),
    ACCESS_TOKEN_INVALID(40102, "ACCESS_TOKEN_INVALID"),
    ACCESS_TOKEN_MISSING(40103, "ACCESS_TOKEN_MISSING"),
    REFRESH_TOKEN_EXPIRED(40104, "REFRESH_TOKEN_EXPIRED"),
    REFRESH_TOKEN_INVALID(40105, "REFRESH_TOKEN_INVALID"),
    REFRESH_TOKEN_MISSING(40106, "REFRESH_TOKEN_MISSING"),;

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
}
