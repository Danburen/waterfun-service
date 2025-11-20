package org.waterwood.waterfunservice.infrastructure.exception;

import org.waterwood.waterfunservice.dto.response.ResponseCode;

public class AuthException extends BusinessException {
    private final String MESSAGE_KEY_PREFIX = "auth";
    public AuthException(int errorCode, String msgKey) {
        super(errorCode,msgKey);
    }

    public AuthException(ResponseCode code) {
        super(code.getCode(), code.getMsgKey());
    }

    public AuthException(ResponseCode code, Object[] params) {
        super(code.getCode(), code.getMsgKey(), params);
    }
}
