package org.waterwood.waterfunservice.service.common;

import lombok.Getter;

@Getter
public enum ServiceErrorCode {
    CLIENT_ERROR(200,"Client error"),
    UNKNOWN_ERROR(5000,"Unknown error"),

    USER_NOT_FOUND(1001,"User not found"),
    USER_DATUM_NOT_FOUND(1002,"User data not found"),
    EMAIL_SEND_FAILED(1003,"Email send failed"),
    SMS_SEND_FAILED(1004,"SMS send failed"),
    EMAIL_SERVICE_NOT_AVAILABLE(1005,"Email service not available"),
    SMS_SERVICE_NOT_AVAILABLE(1006,"SMS service not available"),;
    private final int code;
    private final String message;
    ServiceErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
