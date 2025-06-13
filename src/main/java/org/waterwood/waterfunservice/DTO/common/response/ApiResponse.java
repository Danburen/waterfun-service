package org.waterwood.waterfunservice.DTO.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;

@Builder
@Getter
@AllArgsConstructor
public class ApiResponse<T>{
    Integer code;
    String message;
    T data;
    public ResponseEntity<ApiResponse<T>> toResponseEntity() {
        int httpStatus = (ResponseCode.toHttpStatus(code));
        return ResponseEntity.status(code).body(this);
    }
}
