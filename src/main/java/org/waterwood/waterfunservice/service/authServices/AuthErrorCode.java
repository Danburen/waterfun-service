package org.waterwood.waterfunservice.service.authServices;

import lombok.Getter;

@Getter
public enum AuthErrorCode {
    USER_NOT_FOUND(1,"User not found"),
    USER_DATUM_NOT_FOUND(2,"User data not found"),
    EMAIL_SEND_FAILED(3,"Email send failed"),
    SMS_SEND_FAILED(4,"SMS send failed"),
    EMAIL_SERVICE_NOT_AVAILABLE(5,"Email service not available"),
    SMS_SERVICE_NOT_AVAILABLE(6,"SMS service not available"),;
    private final int number;
    private final String message;
    AuthErrorCode(int number, String message) {
        this.number = number;
        this.message = message;
    }
}
