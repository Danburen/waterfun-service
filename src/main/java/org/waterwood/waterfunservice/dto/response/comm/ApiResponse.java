package org.waterwood.waterfunservice.dto.response.comm;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.waterwood.waterfunservice.dto.common.ServiceResult;
import org.waterwood.waterfunservice.dto.response.ResponseCode;

/**
 * A class to process and store internal response to send to the next layout
 * The <b>code</b> field same with ResponseCode <b>NOT THE STATUS</b>
 * @see ResponseCode
 * @param <T>
 */
@Getter
@AllArgsConstructor
public class ApiResponse<T>{
    Integer code;
    String message;
    T data;
    public static <T> ApiResponse<T> ok(T data){
        return new ApiResponse<>(200,null,data);
    }
    public static ApiResponse<Void> response(ResponseCode responseCode){
        return new ApiResponse<>(responseCode.getCode(),null,null);
    }

    public static ApiResponse<Void> response(ResponseCode responseCode,String message){
        return new ApiResponse<>(responseCode.getCode(),message,null);
    }

    public static <T> ApiResponse<T> success(){
        return new ApiResponse<>(200, "http.success",null);
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(200, "http.success",data);
    }
    public static <T> ApiResponse<T> from(ServiceResult<T> result) {
        return new ApiResponse<>(
                result.getResponseCode().getCode(),
                result.getMessage(),
                result.getData()
        );
    }

    public static ResponseEntity<?> toResponseEntity(ApiResponse<?> apiResponse){
        return ResponseEntity.status(ResponseCode.toHttpStatus(apiResponse.code)).body(apiResponse);
    }

    public ResponseEntity<?> toResponseEntity(){
        return ResponseEntity.status(ResponseCode.toHttpStatus(code)).body(this);
    }

    @JsonIgnore
    public ResponseCode getResponseCode(){
        return ResponseCode.fromCode(this.code);
    }
}
