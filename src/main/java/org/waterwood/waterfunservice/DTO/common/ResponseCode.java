package org.waterwood.waterfunservice.DTO.common;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;
import org.waterwood.waterfunservice.utils.MessageHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum class to store all the response codes
 */
@Getter
public enum ResponseCode {
    // General HTTP Status
    OK(200, "general.success"),
    BAD_REQUEST(400, "general.bad_request"),
    UNAUTHORIZED(401, "general.unauthorized"),
    FORBIDDEN(403, "general.forbidden"),
    NOT_FOUND(404, "general.not_found"),
    INTERNAL_SERVER_ERROR(500, "general.internal_server_error"),
    UNKNOWN_ERROR(50000, "general.unknown_error"),

    // User Validation
    USERNAME_EMPTY_OR_INVALID(40001, "user.validation.username_invalid"),
    PASSWORD_EMPTY_OR_INVALID(40002, "user.validation.password_invalid"),
    USERNAME_OR_PASSWORD_INCORRECT(40003, "user.validation.credentials_incorrect"),
    USER_ALREADY_EXISTS(40017, "user.validation.already_exists"),
    USER_NOT_FOUND(40018, "user.validation.not_found"),

    // Verification Codes
    CAPTCHA_EXPIRED(40004, "verification.captcha.expired"),
    CAPTCHA_INCORRECT(40005, "verification.captcha.incorrect"),
    CAPTCHA_EMPTY(40012, "verification.captcha.empty"),
    VERIFY_CODE_EXPIRED(40006, "verification.code.expired"),
    VERIFY_CODE_INCORRECT(40007, "verification.code.incorrect"),

    // SMS Verification
    SMS_CODE_EXPIRED(40008, "verification.sms.expired"),
    SMS_CODE_INCORRECT(40009, "verification.sms.incorrect"),
    SMS_CODE_EMPTY(40013, "verification.sms.empty"),
    PHONE_NUMBER_EMPTY_OR_INVALID(40015, "verification.phone.invalid"),

    // Email Verification
    EMAIL_CODE_EXPIRED(40010, "verification.email.expired"),
    EMAIL_CODE_INCORRECT(40011, "verification.email.incorrect"),
    EMAIL_CODE_EMPTY(40014, "verification.email.empty"),
    EMAIL_ADDRESS_EMPTY_OR_INVALID(40016, "verification.email_address.invalid"),

    // Role & Permissions
    ROLE_NOT_FOUND(40019, "permission.role.not_found"),
    ROLE_ALREADY_EXISTS(40020, "permission.role.already_exists"),
    PERMISSION_NOT_FOUND(40021, "permission.permission.not_found"),
    PERMISSION_ALREADY_EXISTS(40022, "permission.permission.already_exists"),

    // System
    REDUNDANT_OPERATION(40023, "system.redundant_operation"),
    INVALID_PATH(40024, "system.invalid_path"),
    REQUEST_NOT_IN_WHITELIST(40025, "system.request_not_in_whitelist"),
    INVALID_CONTENT_TYPE(40026, "system.invalid_content_type"),

    // Authentication
    ACCESS_TOKEN_EXPIRED(40101, "auth.access_token.expired"),
    ACCESS_TOKEN_INVALID(40102, "auth.access_token.invalid"),
    ACCESS_TOKEN_MISSING(40103, "auth.access_token.missing"),
    REFRESH_TOKEN_EXPIRED(40104, "auth.refresh_token.expired"),
    REFRESH_TOKEN_INVALID(40105, "auth.refresh_token.invalid"),
    REFRESH_TOKEN_MISSING(40106, "auth.refresh_token.missing"),
    DEVICE_FINGERPRINT_REQUIRED(40107, "auth.device_fingerprint.required");

    private final int code;    // error code
    private final String msgKey;

    ResponseCode(int code, String msgKey) {
        this.code = code;
        this.msgKey = msgKey;
    }


    public ResponseEntity<?> toResponseEntity() {
        Map<String,Object> body = new HashMap<>();
        body.put("code", this.code);
        body.put("message", getMsgKey());
        return ResponseEntity.status(getHttpStatus()).body(body);
    }

    private String getMessage() {
        return MessageHelper.resolveMessage(getMsgKey(),null);
    }

    private String getMessage(Object... args) {
        return MessageHelper.resolveMessage(getMsgKey(),args);
    }

    public <T> ApiResponse<T> toApiResponse(){
        return new ApiResponse<>(this.getCode(), getMessage(),null);
    }

    public <T> ApiResponse<T> toApiResponse(Object args){
        return new ApiResponse<>(this.getCode(), getMessage(args),null);
    }

    public <T> ServiceResult<T> toServiceResult(){
        return new ServiceResult<>(this, this.getCode() == 200, null, null);
    }

    public <T> ServiceResult<T> toServiceResult(String msg){
        return new ServiceResult<>(this, this.getCode() == 200, msg, null);
    }

    public <T> ServiceResult<T> toServiceResult(T data){
        return new ServiceResult<>(this, this.getCode() == 200,null,data);
    }

    public static int toHttpStatus(int code) {
        return (code >= 200 && code <= 600) ? code : code / 100;
    }

    public int getHttpStatus() {
        return toHttpStatus(this.code);
    }

    public @Nullable static ResponseCode fromCode(int code) {
        for (ResponseCode responseCode : ResponseCode.values()) {
            if (responseCode.getCode() == code) {
                return responseCode;
            }
        }
        return null;
    }

    public @Nullable static ResponseCode fromMsgKey(String msgKey) {
        for (ResponseCode responseCode : ResponseCode.values()) {
            if (responseCode.getMsgKey().equals(msgKey)) {
                return responseCode;
            }
        }
        return null;
    }

}
