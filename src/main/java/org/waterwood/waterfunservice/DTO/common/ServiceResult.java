package org.waterwood.waterfunservice.DTO.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.waterwood.waterfunservice.utils.MessageHelper;

/**
 * A class to holder service result.
 * @param <T> the data type of the result
 */
@Builder
@Getter
@AllArgsConstructor
public class ServiceResult <T>{
    private final ResponseCode responseCode;
    private final boolean success;
    private final String message;
    private final T data;

    public static <T> ServiceResult<T> success(T data) {
        return new ServiceResult<>(ResponseCode.OK,true,null,data);
    }
    public static <T> ServiceResult<T> failure(ResponseCode code, String message) {
        return new ServiceResult<>( code,false, message, null );
    }

    public static <T> ServiceResult<T> failure(ResponseCode code) {
        return new ServiceResult<>( code,false,null,null );
    }

    public static ServiceResult<Void> failure(Exception e){
        return new ServiceResult<>( ResponseCode.UNKNOWN_ERROR,false, e.getMessage(), null);
    }


    public static ServiceResult<Void> accept(){
        return new ServiceResult<>( ResponseCode.OK,true,null,null );
    }


    public static <T> ServiceResult<T> response(ResponseCode responseCode){
        return new ServiceResult<>(responseCode, responseCode == ResponseCode.OK, null, null);
    }

    public static <T> ServiceResult<T> emptyDataResponse(ServiceResult<T> serviceResult){
        return new ServiceResult<>(serviceResult.getResponseCode(), serviceResult.isSuccess(), serviceResult.getMessage(), null);
    }

    public static <T> ServiceResult<T> success() {
        return new ServiceResult<>(ResponseCode.OK, true, null, null);
    }

    public static <T> ServiceResult<T> failure(T data){
        return new ServiceResult<>(ResponseCode.INTERNAL_SERVER_ERROR, false, null, data);
    }

    public static <T> ServiceResult<T> failure(String message){
        return new ServiceResult<>(ResponseCode.INTERNAL_SERVER_ERROR, false, message, null);
    }

    public static <T> ServiceResult<T> failure(){
        return new ServiceResult<>(ResponseCode.INTERNAL_SERVER_ERROR, false, null, null);
    }

    public static <T> ServiceResult<T> failure(int code, String msg){
        ResponseCode responseCode = ResponseCode.fromCode(code);
        if (responseCode == null) {
            responseCode = ResponseCode.INTERNAL_SERVER_ERROR;
        }
        return new ServiceResult<>(responseCode, false, msg, null);
    }

    public static ServiceResult<Void> accept(String message) {
        return new ServiceResult<>(ResponseCode.OK, true, message ,null);
    }

    public static ServiceResult<Void> fail(String message) {
        return new ServiceResult<>(ResponseCode.INTERNAL_SERVER_ERROR, false, message,null);
    }

    public static ServiceResult<Void> fail(ResponseCode code, String message) {
        return new ServiceResult<>(code, false, message, null);
    }

    public static ServiceResult<Void> fail(ResponseCode code) {
        return new ServiceResult<>(code, false, null, null);
    }
    public ApiResponse<T> toApiResponse() {
        String finalMessage = null;
        if(! success){
            if (message == null) {
                finalMessage = MessageHelper.resolveMessage(responseCode.getMsgKey(), null);
            } else {
                finalMessage = MessageHelper.resolveMessage(message, null);
            }
        }

        return new ApiResponse<>(
                responseCode.getCode(),
                finalMessage,
                data
        );
    }

    public ResponseEntity<?> toResponseEntity(){
        return ResponseEntity.status(responseCode.getHttpStatus()).body(toApiResponse());
    }
}