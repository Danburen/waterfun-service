package org.waterwood.waterfunservice.infrastructure.exception;

import lombok.Getter;
import org.waterwood.waterfunservice.dto.response.ResponseCode;
import org.waterwood.waterfunservice.infrastructure.utils.io.ResponseCodeMapper;

@Getter
public class BusinessException extends RuntimeException{
    private final String MESSAGE_KEY_PREFIX = "error";
    private final int errorCode;
    private final Object[] params;
    public BusinessException(int errorCode, String msgKey, Object[] params) {
        super(msgKey);
        this.errorCode = errorCode;
        this.params = params;
    }

    public BusinessException(int errorCode, String msgKey) {
        super(msgKey);
        this.errorCode = errorCode;
        this.params = null;
    }

    public BusinessException(ResponseCode code, Object... params) {
        super(code.getMsgKey());
        this.errorCode = code.getCode();
        this.params = params;
    }

    public BusinessException(ResponseCode code) {
        super(ResponseCodeMapper.toNoArgsResponseCode(code).getMsgKey());
        this.errorCode = code.getCode();
        this.params = null;
    }

    public String getFullMessageKey(){
        return MESSAGE_KEY_PREFIX + "." + getMessage();
    }
}
