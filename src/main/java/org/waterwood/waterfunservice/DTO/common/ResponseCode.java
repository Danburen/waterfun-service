package org.waterwood.waterfunservice.DTO.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK(200, "SUCCESS"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "NOT_FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL_SERVER_ERROR"),

    UNKNOWN_ERROR(50000, "UNKNOWN_ERROR"),

    // User-info-related Errors
    USERNAME_EMPTY_OR_INVALID(40001, "USERNAME_EMPTY_OR_INVALID"),
    PASSWORD_EMPTY_OR_INVALID(40002, "PASSWORD_EMPTY_OR_INVALID"),
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
    PHONE_NUMBER_EMPTY_OR_INVALID(40015, "PHONE_NUMBER_EMPTY_OR_INVALID"),
    EMAIL_ADDRESS_EMPTY_OR_INVALID(40016, "EMAIL_ADDRESS_EMPTY_OR_INVALID"),
    USER_ALREADY_EXISTS(40017, "USER_ALREADY_EXISTS"),
    USER_NOT_FOUND(40018, "USER_NOT_FOUND"),
    ROLE_NOT_FOUND(40019, "ROLE_NOT_FOUND"),
    ROLE_ALREADY_EXISTS(40020, "ROLE_ALREADY_EXISTS"),
    PERMISSION_NOT_FOUND(40021, "PERMISSION_NOT_FOUND"),
    PERMISSION_ALREADY_EXISTS(40022, "PERMISSION_ALREADY_EXISTS"),

    REDUNDANT_OPERATION(40023, "REDUNDANT_OPERATION"),
    INVALID_PATH(40024, "INVALID_PATH"),
    REQUEST_NOT_IN_WHITELIST(40025, "REQUEST_NOT_IN_WHITELIST"),
    INVALID_CONTENT_TYPE(40026, "INVALID_CONTENT_TYPE"),

    // Authentication Errors
    ACCESS_TOKEN_EXPIRED(40101, "ACCESS_TOKEN_EXPIRED"),
    ACCESS_TOKEN_INVALID(40102, "ACCESS_TOKEN_INVALID"),
    ACCESS_TOKEN_MISSING(40103, "ACCESS_TOKEN_MISSING"),
    REFRESH_TOKEN_EXPIRED(40104, "REFRESH_TOKEN_EXPIRED"),
    REFRESH_TOKEN_INVALID(40105, "REFRESH_TOKEN_INVALID"),
    REFRESH_TOKEN_MISSING(40106, "REFRESH_TOKEN_MISSING"),;

    private final int code;    // error code
    private final String msg; // error message
    public ResponseEntity<?> toResponseEntity() {
        Map<String,Object> body = new HashMap<>();
        body.put("code", this.code);
        body.put("message", this.msg);
        return ResponseEntity.status(getHttpStatus()).body(body);
    }

    public <T> ApiResponse<T> toApiResponse(){
        return new ApiResponse<>(this.getCode(),this.getMsg(),null);
    }

    public <T> ApiResponse<T> toApiResponse(T data){
        return new ApiResponse<>(this.getCode(),this.getMsg(),data);
    }

    public static int toHttpStatus(int code) {
        return (code >= 200 && code <= 600) ? code : code / 100;
    }

    public int getHttpStatus() {
        return toHttpStatus(this.code);
    }
}
