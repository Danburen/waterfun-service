package org.waterwood.waterfunservice.infrastructure.utils.io;

import org.waterwood.waterfunservice.dto.response.ResponseCode;

public class ResponseCodeMapper {
    public static ResponseCode toNoArgsResponseCode(ResponseCode responseCode){
        return switch (responseCode){
            case OK -> ResponseCode.OK;
            case NOT_FOUND -> ResponseCode.HTTP_NOT_FOUND;
            case FORBIDDEN -> ResponseCode.HTTP_FORBIDDEN;
            default -> responseCode;
        };
    }
}
