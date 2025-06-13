package org.waterwood.waterfunservice.DTO.common.result;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.waterwood.waterfunservice.DTO.common.ResponseCode;
import org.waterwood.waterfunservice.DTO.common.response.ApiResponse;
import org.waterwood.waterfunservice.DTO.common.response.LoginResponseData;

public record AuthResult(Boolean success, @NonNull ResponseCode code) {
    public AuthResult(Boolean success) {
        this(success, success ? ResponseCode.SUCCESS : ResponseCode.BAD_REQUEST);
    }

    public ApiResponse<LoginResponseData> toApiResponse(@Nullable LoginResponseData loginResponseData) {
        return ApiResponse.<LoginResponseData>builder()
                .code(code.getCode())
                .message(code.getMsg())
                .data(loginResponseData).build();

    }

    public ApiResponse<LoginResponseData> toLoginResponse() {
        return ApiResponse.<LoginResponseData>builder()
                .code(code.getCode())
                .message(code.getMsg())
                .data(null).build();
    }
}
