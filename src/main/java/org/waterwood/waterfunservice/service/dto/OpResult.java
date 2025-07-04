package org.waterwood.waterfunservice.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.waterwood.waterfunservice.DTO.common.ErrorType;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.service.common.ServiceErrorCode;

@Getter
@AllArgsConstructor
@Builder
public class OpResult<T> {
    private boolean trySuccess;
    private @Nullable final ServiceErrorCode serviceErrorCode;
    private @Nullable final ResponseCode responseCode;
    private @Nullable final ErrorType errorType;
    private @Nullable final String message;
    private final T resultData;

    public static OpResult<Void> failure(String message){
        return OpResult.<Void>builder()
                .trySuccess(false)
                .errorType(ErrorType.CLIENT)
                .responseCode(ResponseCode.BAD_REQUEST)
                .message(message)
                .build();
    }

    public static OpResult<Void> failure(ResponseCode responseCode){
        return OpResult.<Void>builder()
                .trySuccess(false)
                .errorType(ErrorType.CLIENT)
                .responseCode(responseCode)
                .build();
    }


    public static <E> OpResult<E> failure(ResponseCode responseCode, String message){
        return OpResult.<E>builder()
                .trySuccess(false)
                .errorType(ErrorType.CLIENT)
                .responseCode(responseCode)
                .message(message)
                .build();
    }

    public static OpResult<Void> success(){
        return OpResult.<Void>builder()
                .trySuccess(true)
                .responseCode(ResponseCode.OK)
                .build();
    }

    public static OpResult<Void> success(String message){
        return OpResult.<Void>builder()
                .trySuccess(true)
                .responseCode(ResponseCode.OK)
                .message(message)
                .build();
    }

    public static <T> OpResult<T> success(T data){
        return OpResult.<T>builder()
                .trySuccess(true)
                .responseCode(ResponseCode.OK)
                .resultData(data)
                .build();
    }
}
