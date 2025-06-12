package org.waterwood.waterfunservice.DTO.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder
public class LoginResponse {
    private boolean success;
    private Integer code;
    private String message;
    private String accessToken;
    private String refreshToken;
    private Long userId;
    private String username;
}
