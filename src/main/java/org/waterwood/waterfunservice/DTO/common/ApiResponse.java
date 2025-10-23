package org.waterwood.waterfunservice.DTO.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
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
        return new ApiResponse<>(responseCode.getCode(),null,null);
    }

    public boolean isSuccess(){
        return code == 200;
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

    public ResponseCode getResponseCode(){
        return ResponseCode.fromCode(this.code);
    }
}
