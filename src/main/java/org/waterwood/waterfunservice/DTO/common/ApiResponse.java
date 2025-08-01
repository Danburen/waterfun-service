package org.waterwood.waterfunservice.DTO.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.ResponseEntity;

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

    public static <T> ApiResponse<T> emptyDataResponse(ApiResponse<T> apiResponse){
        return new ApiResponse<>(apiResponse.getCode(), apiResponse.getMessage(), null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200,null,data);
    }
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200,null,null);
    }

    public static <T> ApiResponse<T> failure(T data){
        return new ApiResponse<>(500,"INTERNAL_SERVER_ERROR",data);
    }

    public static <T> ApiResponse<T> failure(String message){
        return new ApiResponse<>(500,message,null);
    }

    public static <T> ApiResponse<T> failure(){
        return new ApiResponse<T>(500,"INTERNAL_SERVER_ERROR",null);
    }

    public static <T> ApiResponse<T> failure(ResponseCode responseCode){
        return new ApiResponse<T>(responseCode.getCode(),responseCode.getMsg(),null);
    }

    public static <T> ApiResponse<T> failure(ResponseCode responseCode,String message){
        return new ApiResponse<T>(responseCode.getCode(),message,null);
    }

    public static <T> ApiResponse<T> failure(int code,String msg){
        return new ApiResponse<T>(code,msg,null);
    }

    public static ApiResponse<Void> accept() {
        return new ApiResponse<>(200, null, null);
    }

    public static ApiResponse<Void> accept(String message) {
        return  new ApiResponse<>(200,message,null);
    }

    public static ApiResponse<Void> fail(String message) {
        return new ApiResponse<>(500,message,null);
    }

    public static ApiResponse<Void> fail(ResponseCode code,String message) {
        return new ApiResponse<>(code.getCode(),message,null);
    }

    public static ApiResponse<Void> fail(ResponseCode code) {
        return new ApiResponse<>(code.getCode(),code.getMsg(),null);
    }

    public boolean isSuccess(){
        return code == 200;
    }

    public static ResponseEntity<?> toResponseEntity(ApiResponse<?> apiResponse){
        return ResponseEntity.status(ResponseCode.toHttpStatus(apiResponse.code)).body(apiResponse);
    }

    public ResponseEntity<?> toResponseEntity(){
        return ResponseEntity.status(ResponseCode.toHttpStatus(code)).body(this);
    }

    public ResponseCode getResponseCode(){
        return ResponseCode.fromCode(this.code);
    }
}
