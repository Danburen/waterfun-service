package org.waterwood.waterfunservice.DTO.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * A class to process and store internal response to send to the next layout
 * The <b>code</b> field same with ResponseCode <b>NOT THE STATUS</b>
 * @see ResponseCode
 * @param <T>
 */
@Builder
@Getter
@AllArgsConstructor
public class ApiResponse<T>{
    Integer code;
    String message;
    T data;
    public static ApiResponse<Void> response(ResponseCode responseCode){
        return new ApiResponse<>(responseCode.getCode(),responseCode.getMsg(),null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200,null,data);
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<T>(200,null, null);
    }

    public static <T> ApiResponse<T> failure(T data){
        return new ApiResponse<>(500,"INTERNAL_SERVER_ERROR",data);
    }

    public static <T> ApiResponse<T> failure(){
        return new ApiResponse<T>(500,"INTERNAL_SERVER_ERROR",null);
    }

    public static <T> ApiResponse<T> failure(ResponseCode responseCode){
        return new ApiResponse<T>(responseCode.getCode(),responseCode.getMsg(),null);
    }

    public boolean isSuccess(){
        return code == 200;
    }
}
