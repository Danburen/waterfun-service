package org.waterwood.waterfunservice.DTO.common.result;

import jakarta.annotation.Nullable;
import org.waterwood.waterfunservice.DTO.common.ErrorCode;
import org.waterwood.waterfunservice.DTO.common.response.LoginResponse;

public record AuthResult(Boolean success, @Nullable ErrorCode code) {
    public AuthResult(Boolean success) {
        this(success, null);
    }
    public LoginResponse toLoginResponse(LoginResponse loginResponse) {
        if (code != null) {
            return code.toLoginResponseBuilder(loginResponse).success(success).build();
        }
        return LoginResponse.builder()
                .success(success)
                .code(null)
                .message(null)
                .accessToken(loginResponse.getAccessToken())
                .userId(loginResponse.getUserId())
                .username(loginResponse.getUsername())
                .build();
    }

    public LoginResponse toLoginResponse() {
        if (code != null) {
            return code.toLoginResponseBuilder().success(success).build();
        }
        return LoginResponse.builder()
                .success(success)
                .code(null)
                .message(null)
                .build();
    }
}
